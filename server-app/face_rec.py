# USAGE
# python webstreaming.py --ip 0.0.0.0 --port 8000
# import the necessary packages
from imutils.video import VideoStream
from flask import Response, jsonify
from flask import Flask
from flask import render_template
from flask import send_file
import threading
import argparse
import face_recognition
import cv2
import numpy as np
import json
import os
import timeit
import time
import pickle

# CONFIG
IP_ADDRESS = "0.0.0.0"
PORT = 5000
FACE_INFO_FOLDER = "faces" #relative to face_rec.py
FACE_INFO_CONFIG = "face_info.json"
PICKLE_INPUT_FILE = "encodings.dat"

FACE_DISTANCE_THRESHOLD = 40

RUN_ON_PI = (hasattr(os, 'uname') and os.uname().machine == 'armv7l') # detect if we are running on raspberry pi by CPU architecture


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
lock = threading.Lock() # lock for outputFrame so that it is not read while being updated

# initialize a flask object
app = Flask(__name__)


# Get a reference to webcam #0 (the default one)
print("Acquiring VideoStream")
if(RUN_ON_PI):
    print("Pi environment detected")
    vs = VideoStream(src=0, usePiCamera=True,resolution=(480,640)).start()
    time.sleep(2.0) # the camera needs time to start, if loading encodings directly it can be too fast
else:
    print("Non-Pi environment")
    # TODO doesn't work on windows? webcam turns on, but no video in video_feed
    from imutils.video import WebcamVideoStream
    vs = WebcamVideoStream(src=0).start()
    time.sleep(2.0)

# video_capture = cv2.VideoCapture(0)

known_face_encodings = []
known_face_names = []

# load the face info
# def load_face_info():
#     start_time = timeit.default_timer()
#     print("Creating face encodings")
#     #face_info = []
#     # get the relations between image file and people
#     dirname = os.path.dirname(__file__)
#     index_file_path = os.path.join(dirname, FACE_INFO_FOLDER, FACE_INFO_CONFIG)
#     with open(index_file_path, 'r') as indexfile:
#         json_info = json.load(indexfile)
#
#         for person in json_info['people']:
#             print("Load face info for {name}".format(name=person['name']))
#             # assume images for now to be in, eg, faces/obama/obama.jpg
#             face_file_path = os.path.join(dirname, FACE_INFO_FOLDER, person['folder'], person['folder']+'.jpg')
#             person_image = face_recognition.load_image_file(face_file_path)
#             person_face_encoding = face_recognition.face_encodings(person_image)[0]
#
#             known_face_encodings.append(person_face_encoding)
#             known_face_names.append(person['name'])
#
#         for person in json_info['people']:
#             json_face_info[person['name']] = person['ID']
#
#     stop_time = timeit.default_timer()
#
#     print("Time to load faces: {time}\n".format(time=(stop_time-start_time)))

def load_face_info():
    start_time = timeit.default_timer()
    print("Creating face encodings")
    global known_face_names, known_face_encodings

    # TODO: check for pickle file existance and call generation script if it doesn't exist?
    dirname = os.path.dirname(__file__)
    pickle_file_path = os.path.join(dirname, FACE_INFO_FOLDER, PICKLE_INPUT_FILE)
    print("Pickle file: {fname}".format(fname=pickle_file_path))
    with open(pickle_file_path, 'rb') as picklefile:
        face_info = pickle.load(picklefile)

        known_face_encodings = face_info['encodings']
        known_face_names = face_info['names']

        for i in range(0, len(face_info['names'])):
            json_face_info[face_info['names'][i]] = face_info['ids'][i]

    stop_time = timeit.default_timer()
    print("Time to load faces: {time}\n".format(time=(stop_time - start_time)))


# def load_face_info_orig():
#     start_time = timeit.default_timer()
#     print("Creating face encodings")
#     # face_info = []
#     # get the relations between image file and people
#     dirname = os.path.dirname(__file__)
#     index_file_path = os.path.join(dirname, FACE_INFO_FOLDER, FACE_INFO_CONFIG)
#     with open(index_file_path, 'r') as indexfile:
#         json_info = json.load(indexfile)
#
#         for person in json_info['people']:
#             print("Load face info for {name}".format(name=person['name']))
#             # assume images for now to be in, eg, faces/obama/obama.jpg
#             face_file_path = os.path.join(dirname, FACE_INFO_FOLDER, person['folder'], person['folder'] + '.jpg')
#             person_image = face_recognition.load_image_file(face_file_path)
#             person_face_encoding = face_recognition.face_encodings(person_image)[0]
#
#             known_face_encodings.append(person_face_encoding)
#             known_face_names.append(person['name'])
#
#     stop_time = timeit.default_timer()
#
#     print("Time to load faces: {time}\n".format(time=(stop_time - start_time)))

load_face_info()
print(known_face_names)

def confidence_from_distance(dist):
    conf = (FACE_DISTANCE_THRESHOLD - dist) / FACE_DISTANCE_THRESHOLD * 100
    #conf = (100-dist)
    return conf


def recognize_face(frameCount):
    # Initialize some variables
    face_locations = []
    face_encodings = []
    face_names = []
    process_this_frame = True

    global vs, outputFrame, lock, info, recognized_person

    total = 0
    FRAME_SCALE_FACTOR = 4 # frame divided in size by this number
    while True:
        # Grab a single frame of video
        frame = vs.read()

        # Resize frame of video to 1/4 size for faster face recognition processing
        small_frame = cv2.resize(frame, (0, 0), fx=1/FRAME_SCALE_FACTOR, fy=1/FRAME_SCALE_FACTOR)

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
                # NOTE: we get the distance to EVERY face in the encodings
                best_match_index = np.argmin(face_distances)
                best_match_confidence = confidence_from_distance(best_match_index)
                if matches[best_match_index]:
                    name = known_face_names[best_match_index]
                    if best_match_index < FACE_DISTANCE_THRESHOLD:
                        print("Matched {name} with distance {dist} confidence {conf}%".format(name=name, dist=best_match_index, conf=best_match_confidence))
                    else:
                        print("Ignoring Match {name} with distance {dist} below threshold {thresh}".format(name=name, dist=best_match_index, thresh=FACE_DISTANCE_THRESHOLD))
                        name = "Unknown"

                face_names.append(name)

                if name == "Unknown":
                    recognized_person = {"name": "Unknown", "ID": "", "conf": 0}
                else:
                    recognized_person = {"name": name, "ID": json_face_info[name], "conf": best_match_confidence}

        #process_this_frame = not process_this_frame
        process_this_frame = total % 4 # every 4th frame instead of every 2nd

        if total > frameCount:
            # Display the results
            for (top, right, bottom, left), name in zip(face_locations, face_names):
                # Scale back up face locations since the frame we detected in was scaled to 1/4 size
                top *= FRAME_SCALE_FACTOR
                right *= FRAME_SCALE_FACTOR
                bottom *= FRAME_SCALE_FACTOR
                left *= FRAME_SCALE_FACTOR

                # Draw a box around the face
                cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 255), 2)

                # Draw a label with a name below the face
                cv2.rectangle(frame, (left, bottom - 35), (right, bottom), (0, 0, 255), cv2.FILLED)
                font = cv2.FONT_HERSHEY_DUPLEX
                cv2.putText(frame, name, (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)

                # Put the confidence level in the top left if the person was recognized
                #cv2.rectangle(frame, (0,0), ())
                if(recognized_person["name"] != "Unknown"):
                    cv2.putText(frame, "Conf:" + str(recognized_person['conf'])+"%", (0, 30), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (52, 235, 232), 2)


        total += 1

        # Display the resulting image
        # acquire the lock, set the output frame, and release the
        # lock
        with lock:
            outputFrame = frame.copy()


# Generator that allows the video stream to be returned frame by frame
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

            # set the outputFrame to None so that we do not loop on the same frame
            # It *seems* to reduce Pi 4 CPU usage by about 10% on the main core
            # TODO: does this make any real difference in practice?
            outputFrame = None

            # ensure the frame was successfully encoded
            if not flag:
                continue

        # yield the output frame in the byte format
        yield (b'--frame\r\n' b'Content-Type: image/jpeg\r\n\r\n' +
               bytearray(encodedImage) + b'\r\n')


@app.route("/person_info")
def info_stream():
    return jsonify(recognized_person)
        
@app.route("/")
def index():
    # return the rendered template
    return render_template("index.html")


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
