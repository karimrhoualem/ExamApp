# USAGE
# python face_rec.py --ip 0.0.0.0 --port 8000
# import the necessary packages
# Referenced :
# 1 - https://github.com/ageitgey/face_recognition/blob/master/examples/facerec_from_webcam_faster.py
# 2 - https://github.com/ageitgey/face_recognition
import argparse
import math
import os
import pickle
import threading
import time
import timeit

import cv2
import face_recognition
import numpy as np
from flask import Flask
from flask import Response, jsonify
from flask import render_template
from imutils.video import VideoStream

# CONFIG
IP_ADDRESS = "192.168.0.91"
PORT = 5000
FACE_INFO_FOLDER = "faces"  # relative to face_rec.py
FACE_INFO_CONFIG = "face_info.json"
PICKLE_INPUT_FILE = "encodings.dat"

# Face Detection and recognition Constants
FACE_DISTANCE_THRESHOLD = 25
FACE_COMPARE_STRICTNESS = 0.5

# detect if we are running on raspberry pi by CPU architecture
RUN_ON_PI = (hasattr(os,
                     'uname') and os.uname().machine == 'armv7l')

outputFrame = None
info = None
json_face_info = {}
recognized_person = {}
lock = threading.Lock()  # lock for outputFrame so that it is not read while being updated

# initialize a flask object
app = Flask(__name__)

# Get a reference to webcam #0 (the default one)
print("Acquiring VideoStream")
if RUN_ON_PI:
    print("Pi environment detected")
    vs = VideoStream(src=0, usePiCamera=True, resolution=(640, 480)).start()
    # the camera needs time to start, if loading encodings directly it can be too fast
    time.sleep(2.0)
else:
    print("Non-Pi environment")
    # TODO doesn't work on windows? webcam turns on, but no video in video_feed
    from imutils.video import WebcamVideoStream

    vs = WebcamVideoStream(src=0).start()
    time.sleep(2.0)

# Person Info Variables
known_face_encodings = []
known_face_names = []


# Load Face Info Method
def load_face_info():
    start_time = timeit.default_timer()
    print("Creating face encodings")
    global known_face_names, known_face_encodings

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


load_face_info()
print(known_face_names)


def confidence_from_distance(dist):
    conf = (FACE_DISTANCE_THRESHOLD - dist) / FACE_DISTANCE_THRESHOLD * 100
    return round(conf, 1)


# From library https://github.com/ageitgey/face_recognition/wiki/Calculating-Accuracy-as-a-Percentage
def face_distance_to_conf(face_distance, face_match_threshold=FACE_COMPARE_STRICTNESS):
    if face_distance > face_match_threshold:
        range = (1.0 - face_match_threshold)
        linear_val = (1.0 - face_distance) / (range * 2.0)
        return linear_val
    else:
        range = face_match_threshold
        linear_val = 1.0 - (face_distance / (range * 2.0))
        return linear_val + ((1.0 - linear_val) * math.pow((linear_val - 0.5) * 2, 0.2))


def recognize_face(frameCount):
    # Initialize some variables
    face_locations = []
    face_encodings = []
    face_names = []
    process_this_frame = True

    global vs, outputFrame, lock, info, recognized_person

    total = 0
    FRAME_SCALE_FACTOR = 3  # frame divided in size by this number
    while True:
        # Grab a single frame of video
        frame = vs.read()

        # Resize frame of video to 1/4 size for faster face recognition processing
        small_frame = cv2.resize(frame, (0, 0), fx=1 / FRAME_SCALE_FACTOR, fy=1 / FRAME_SCALE_FACTOR)

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
                matches = face_recognition.compare_faces(known_face_encodings, face_encoding,
                                                         tolerance=FACE_COMPARE_STRICTNESS)
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
                # best_match_confidence = face_distance_to_conf(best_match_index)
                if matches[best_match_index]:
                    name = known_face_names[best_match_index]
                    if best_match_index < FACE_DISTANCE_THRESHOLD:
                        print("Matched {name} with distance {dist} confidence {conf}%"
                              .format(name=name, dist=best_match_index, conf=best_match_confidence))
                    else:
                        print("Ignoring Match {name} with distance {dist} below threshold {thresh}"
                              .format(name=name, dist=best_match_index, thresh=FACE_DISTANCE_THRESHOLD))
                        name = "Unknown"

                face_names.append(name)

                if name == "Unknown":
                    recognized_person = {"name": "Unknown", "ID": "", "conf": 0}
                else:
                    recognized_person = {"name": name, "ID": json_face_info[name], "conf": best_match_confidence}

        process_this_frame = not process_this_frame
        # process_this_frame = total % 4 # every 4th frame instead of every 2nd

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
                # and their ID in the bottom left
                if (recognized_person["name"] != "Unknown"):
                    cv2.putText(frame, "Conf:" + str(recognized_person['conf']) + "%", (0, 30),
                                cv2.FONT_HERSHEY_SIMPLEX, 1.0, (52, 235, 232), 2)
                    # TODO: dynamically get frame height to place ID
                    cv2.putText(frame, "ID: " + recognized_person["ID"], (0, 600), cv2.FONT_HERSHEY_SIMPLEX, 1.0,
                                (52, 235, 232), 2)

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
