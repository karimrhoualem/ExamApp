import threading
import argparse
import face_recognition
import cv2
import numpy as np
import json
import os
import timeit
import pickle
import random

FACE_INFO_FOLDER = "faces"  # relative to face_rec.py
FACE_INFO_CONFIG = "face_info.json"

LABELLED_FACES_FOLDER ="faces/big-faces-dataset"

PICKLE_OUTPUT_FILE = "encodings.dat"

#known_face_encodings = []
#known_face_names = []

# dict to store all the parallel arrays together
# parallel arrays are so that the encodings array can be directly passed to face_recognition
face_info = {}
face_info['names'] = []
face_info['ids'] = []
face_info['encodings'] = []

start_time = timeit.default_timer()
print("Creating face encodings")

# load the face info
def load_face_info():
    # face_info = []
    # get the relations between image file and people

    dirname = os.path.dirname(__file__)
    index_file_path = os.path.join(dirname, FACE_INFO_FOLDER, FACE_INFO_CONFIG)
    with open(index_file_path, 'r') as indexfile:
        json_info = json.load(indexfile)

        for person in json_info['people']:
            print("Load face info for {name}".format(name=person['name']))
            # assume images for now to be in, eg, faces/obama/obama.jpg
            face_file_path = os.path.join(dirname, FACE_INFO_FOLDER, person['folder'], person['folder'] + '.jpg')
            person_image = face_recognition.load_image_file(face_file_path)
            person_face_encoding = face_recognition.face_encodings(person_image)[0]

            #known_face_encodings.append(person_face_encoding)
            face_info['encodings'].append(person_face_encoding)
            #known_face_names.append(person['name'])
            face_info['names'].append(person['name'])
            face_info['ids'].append(person['ID'])


# load some faces from the labelled faces in the wild dataset
def load_face_extra_dataset():
    dirname = os.getcwd()

    dataset_folder_path = os.path.join(dirname, LABELLED_FACES_FOLDER, "")

    dataset_folders = os.listdir(dataset_folder_path)

    # Use 100 people to simulate a classroom size
    print("First 100 people:")
    subset_folders = random.sample(dataset_folders, 100)

    for person in subset_folders:
        print("Load face info for {name}".format(name=person))
        face_file_folder = os.path.join(dataset_folder_path, person)

        # some of the LFW folders have multiple faces, choose the first
        face_file_path = os.path.join(face_file_folder, os.listdir(face_file_folder)[0])
        #print(face_file_path)

        person_image = face_recognition.load_image_file(face_file_path)
        person_face_encoding = face_recognition.face_encodings(person_image)[0]

        face_info['encodings'].append(person_face_encoding)
        face_info['names'].append(person)
        face_info['ids'].append("0000")

# face info for our images
load_face_info()

# faces from the labelled faces in the wild dataset, to make the dataset larger
load_face_extra_dataset()

stop_time = timeit.default_timer()
print("Time to load faces: {time}\n".format(time=(stop_time - start_time)))

pickle_file_path = os.path.join(FACE_INFO_FOLDER, PICKLE_OUTPUT_FILE)
with open(pickle_file_path, 'wb') as picklefile:
    pickle.dump(face_info, picklefile)
    print('Wrote encodings pickled to {fname}'.format(fname=pickle_file_path))

print('Loaded {count} encodings'.format(count=len(face_info['encodings'])))