import json
import os
import pickle
import random
import timeit

import face_recognition
import image_to_numpy

import PIL.Image
import PIL.ImageOps
import numpy as np
from PIL import Image, ExifTags

# {"name": "Paulina Aviles",  "ID": "40109825", "folder": "paulina"},

FACE_INFO_FOLDER = "faces"  # relative to face_rec.py
FACE_INFO_CONFIG = "face_info.json"

LABELLED_FACES_FOLDER = "faces/big-faces-dataset"

PICKLE_OUTPUT_FILE = "encodings.dat"
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
    dirname = os.path.dirname(__file__)
    index_file_path = os.path.join(dirname, FACE_INFO_FOLDER, FACE_INFO_CONFIG)
    with open(index_file_path, 'r') as indexfile:
        json_info = json.load(indexfile)

        for person in json_info['people']:
            print("Load face info for {name}".format(name=person['name']))
            # assume images for now to be in, eg, faces/obama/obama.jpg
            face_file_path = os.path.join(dirname, FACE_INFO_FOLDER, person['folder'], person['folder'] + '.jpg')
            #img = image_to_numpy.load_image_file(face_file_path)
            rotate_images(face_file_path)
            person_image = face_recognition.load_image_file(face_file_path)
            person_face_encoding = face_recognition.face_encodings(person_image, num_jitters=100)[0]

            # known_face_encodings.append(person_face_encoding)
            face_info['encodings'].append(person_face_encoding)
            # known_face_names.append(person['name'])
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
        face_file_folder = os.path.join(dataset_folder_path, person)

        # some of the LFW folders have multiple faces, choose the first
        face_file_path = os.path.join(face_file_folder, os.listdir(face_file_folder)[0])
        print("Load face info for {name} from {path}".format(name=person, path=face_file_path))
        # print(face_file_path)

        try:
            #img = image_to_numpy.load_image_file(face_file_path)
            rotate_images(face_file_path)
            person_image = face_recognition.load_image_file(face_file_path)
            person_face_encoding = face_recognition.face_encodings(person_image)[0]

            face_info['encodings'].append(person_face_encoding)
            face_info['names'].append(person)
            face_info['ids'].append("0000")
        # Some photos in the LFW dataset don't yield valid face encodings, and thus an index error occurs
        except Exception as e:
            print("ERROR: Could not encode face for {name}".format(name=person))
            print(e)

#Rotate images to correct orientation
#References :
# 1 - https://stackoverflow.com/questions/13872331/rotating-an-image-with-orientation-specified-in-exif-using-python-without-pil-in
# 2 - https://medium.com/@ageitgey/the-dumb-reason-your-fancy-computer-vision-app-isnt-working-exif-orientation-73166c7d39da
def exif_transpose(img):
    if not img:
        return img

    exif_orientation_tag = 274

    # Check for EXIF data (only present on some files)
    if hasattr(img, "_getexif") and isinstance(img._getexif(), dict) and exif_orientation_tag in img._getexif():
        exif_data = img._getexif()
        orientation = exif_data[exif_orientation_tag]

        # Handle EXIF Orientation
        if orientation == 1:
            # Normal image - nothing to do!
            pass
        elif orientation == 2:
            # Mirrored left to right
            img = img.transpose(PIL.Image.FLIP_LEFT_RIGHT)
        elif orientation == 3:
            # Rotated 180 degrees
            img = img.rotate(180)
        elif orientation == 4:
            # Mirrored top to bottom
            img = img.rotate(180).transpose(PIL.Image.FLIP_LEFT_RIGHT)
        elif orientation == 5:
            # Mirrored along top-left diagonal
            img = img.rotate(-90, expand=True).transpose(PIL.Image.FLIP_LEFT_RIGHT)
        elif orientation == 6:
            # Rotated 90 degrees
            img = img.rotate(-90, expand=True)
        elif orientation == 7:
            # Mirrored along top-right diagonal
            img = img.rotate(90, expand=True).transpose(PIL.Image.FLIP_LEFT_RIGHT)
        elif orientation == 8:
            # Rotated 270 degrees
            img = img.rotate(90, expand=True)

    return img


def rotate_images(filepath):
    try:
        image = Image.open(filepath)
        image = exif_transpose(image)

        image.save(filepath)
        image.close()
    except (AttributeError, KeyError, IndexError):
        # cases: image don't have getexif
        pass


# face info for our images
load_face_info()

# faces from the labelled faces in the wild dataset, to make the dataset larger
# load_face_extra_dataset()

stop_time = timeit.default_timer()
print("Time to load faces: {time}\n".format(time=(stop_time - start_time)))

pickle_file_path = os.path.join(FACE_INFO_FOLDER, PICKLE_OUTPUT_FILE)
with open(pickle_file_path, 'wb') as picklefile:
    pickle.dump(face_info, picklefile)
    print('Wrote encodings pickled to {fname}'.format(fname=pickle_file_path))

print('Loaded {count} encodings'.format(count=len(face_info['encodings'])))
