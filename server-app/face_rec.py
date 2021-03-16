# import the necessary packages
from imutils.video import VideoStream
from flask import Response
from flask import Flask
from flask import render_template
from flask import jsonify
import threading
import argparse
import face_recognition
import cv2
import numpy as np
import json
import os
import timeit


# CONFIG
IP_ADDRESS = "192.168.2.135"
PORT = 5000
FACE_INFO_FOLDER = "faces"  # relative to face_rec.py
FACE_INFO_CONFIG = "face_info.json"
RUN_ON_PI = (os.uname().machine == 'armv7l')  # detect if we are running on raspberry pi by CPU architecture

# This is a demo of running face recognition on live video from your webcam. It's a little more complicated than the
# other example, but it includes some basic performance tweaks to make things run a lot faster:
#   1. Process each video frame at 1/4 resolution (though still display it at full resolution)
#   2. Only detect faces in every other frame of video.

# PLEASE NOTE: This example requires OpenCV (the `cv2` library) to be installed only to read from your webcam.
# OpenCV is *not* required to use the face_recognition library. It's only required if you want to run this
# specific demo. If you have trouble installing it, try any of the other demos that don't require it instead.

# initialize the output frame and a lock used to ensure thread-safe
# exchanges of the output frames (useful for multiple browsers/tabs
# are viewing the stream)
outputFrame = None
info = None
json_face_info = {}
recognized_person = {}
lock = threading.Lock()

# initialize a flask object
app = Flask(__name__)

# Get a reference to webcam #0 (the default one)
print("Acquiring VideoStream")
if RUN_ON_PI:
    vs = VideoStream(src=0, usePiCamera=True, resolution=(480, 640)).start()
else:
    vs = VideoStream(src=0).start()
# video_capture = cv2.VideoCapture(0)

# Load a sample picture and learn how to recognize it.
# obama_image = face_recognition.load_image_file("Obama.jpg")
# obama_face_encoding = face_recognition.face_encodings(obama_image)[0]

# Load a second sample picture and learn how to recognize it.
# ahmed_image = face_recognition.load_image_file("Ahmed2.jpeg")
# ahmed_face_encoding = face_recognition.face_encodings(ahmed_image)[0]

# Create arrays of known face encodings and their names

known_face_encodings = []
known_face_names = []


# load the face info
def load_face_info():
    # face_info = []
    # get the relations between image file and people
    start_time = timeit.default_timer()
    print("Creating face encodings")
    global json_face_info
    index_file_path = os.path.join(FACE_INFO_FOLDER, FACE_INFO_CONFIG)
    with open(index_file_path, 'r') as indexfile:
        json_info = json.load(indexfile)

        for person in json_info['people']:
            print("Load face info for {name}".format(name=person['name']))
            # assume images for now to be in, eg, faces/obama/obama.jpg
            face_file_path = os.path.join(FACE_INFO_FOLDER, person['folder'], person['folder'] + '.jpg')
            person_image = face_recognition.load_image_file(face_file_path)
            person_face_encoding = face_recognition.face_encodings(person_image)[0]

            known_face_encodings.append(person_face_encoding)
            known_face_names.append(person['name'])

        for person in json_info['people']:
            json_face_info[person['name']] = person['ID']

        stop_time = timeit.default_timer()
        print("Time to load faces: {time}\n".format(time=(stop_time - start_time)))


load_face_info()


@app.route("/")
def index():
    # return the rendered template
    return render_template("index.html")

    # Initialize some variables
    face_locations = []
    face_encodings = []
    face_names = []
    process_this_frame = True


def recognize_face(frameCount):
    # Initialize some variables
    face_locations = []
    face_encodings = []
    face_names = []
    process_this_frame = True

    global info, recognized_person

    global vs, outputFrame, lock

    total = 0
    while True:
        # Grab a single frame of video
        frame = vs.read()

        # Resize frame of video to 1/4 size for faster face recognition processing
        small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)

        # Convert the image from BGR color (which OpenCV uses) to RGB color (which face_recognition uses)
        rgb_small_frame = small_frame[:, :, ::-1]

        # Only process every other frame of video to save time
        if process_this_frame:
            # Find all the faces and face encodings in the current frame of video
            face_locations = face_recognition.face_locations(rgb_small_frame)
            face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

            face_names = []
            for face_encoding in face_encodings:
                # See if the face is a match for the known face(s)
                matches = face_recognition.compare_faces(known_face_encodings, face_encoding)
                name = "Unknown"

                # # If a match was found in known_face_encodings, just use the first one.
                # if True in matches:
                #     first_match_index = matches.index(True)
                #     name = known_face_names[first_match_index]

                # Or instead, use the known face with the smallest distance to the new face
                face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
                best_match_index = np.argmin(face_distances)
                if matches[best_match_index]:
                    name = known_face_names[best_match_index]

                face_names.append(name)

                if name == "Unknown":
                    recognized_person = {"name": "Unknown", "ID": ""}
                else:
                    recognized_person = {"name": name, "ID": json_face_info[name]}

        process_this_frame = not process_this_frame

        if total > frameCount:
            # Display the results
            for (top, right, bottom, left), name in zip(face_locations, face_names):
                # Scale back up face locations since the frame we detected in was scaled to 1/4 size
                top *= 4
                right *= 4
                bottom *= 4
                left *= 4

                # Draw a box around the face
                cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 255), 2)

                # Draw a label with a name below the face
                cv2.rectangle(frame, (left, bottom - 35), (right, bottom), (0, 0, 255), cv2.FILLED)
                font = cv2.FONT_HERSHEY_DUPLEX
                cv2.putText(frame, name, (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)

        total += 1

        # Display the resulting image

        # acquire the lock, set the output frame, and release the
        # lock
        with lock:
            outputFrame = frame.copy()


def generate_stream():
    # grab global references to the output frame and lock variables
    global outputFrame, lock, encodedImage

    # loop over frames from the output stream
    while True:
        # wait until the lock is acquired
        with lock:
            # check if the output frame is available, otherwise skip
            # the iteration of the loop
            if outputFrame is None:
                continue

            # encode the frame in JPEG format
            (flag, encodedImage) = cv2.imencode(".jpeg", outputFrame)

            # ensure the frame was successfully encoded
            if not flag:
                continue

        # yield the output frame in the byte format
        yield (b'--frame\r\n' b'Content-Type: image/jpeg\r\n\r\n' +
               bytearray(encodedImage) + b'\r\n')


@app.route("/person_info")
def info_stream():
    return jsonify(recognized_person)


# @app.route("/get_image")
# def get_image():
# return send_file("<link to jpeg>", mimetype="image/jpeg")


@app.route("/video_feed")
def video_feed():
    # return the response generated along with the specific media
    # type (mime type)
    return Response(generate_stream(),
                    mimetype="multipart/x-mixed-replace; boundary=frame")


# check to see if this is the main thread of execution
if __name__ == '__main__':
    # construct the argument parser and parse command line arguments
    ap = argparse.ArgumentParser()
    ap.add_argument("-f", "--frame-count", type=int, default=32,
                    help="# of frames used to construct the background model")
    args = vars(ap.parse_args())

    # start a thread that will perform motion detection
    t = threading.Thread(target=recognize_face, args=(
        args["frame_count"],))
    t.daemon = True
    t.start()

    # start the flask app
    app.run(host=IP_ADDRESS, port=PORT, debug=True,
            threaded=True, use_reloader=False)

# Release handle to the webcam
vs.stop()
cv2.destroyAllWindows()
