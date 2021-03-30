'''
This file recreates encodings.dat, but only the ID section (doesn't recreate the face encodings)
'''


import face_recognition
import json
import os
import timeit
import pickle

FACE_INFO_FOLDER = "faces"  # relative to face_rec.py
FACE_INFO_CONFIG = "face_info.json"

LABELLED_FACES_FOLDER ="faces/big-faces-dataset"

PICKLE_FILE = "encodings.dat"

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
def regenerate_id_in_face_info():
    global face_info

    names_new = []
    id_new = []

    dirname = os.path.dirname(__file__)
    pickle_file_path = os.path.join(dirname, FACE_INFO_FOLDER, PICKLE_FILE)
    print("Pickle file: {fname}".format(fname=pickle_file_path))
    with open(pickle_file_path, 'rb') as picklefile:
        face_info = pickle.load(picklefile)

    index_file_path = os.path.join(dirname, FACE_INFO_FOLDER, FACE_INFO_CONFIG)
    with open(index_file_path, 'r') as indexfile:
        json_info = json.load(indexfile)

        for person in json_info['people']:
            print("Load ID {id} for {name}".format(id=person['ID'], name=person['name']))
            #face_info['names'].append(person['name'])
            #face_info['ids'].append(person['ID'])
            names_new.append(person['name'])
            id_new.append(person['ID'])

    if(len(names_new) != len(face_info['names'])):
        print("Length mismatch between encodings file and json file! aborting...")
        exit(1)
    else:
        face_info['names'] = names_new
        face_info['ids'] = id_new

    pickle_file_path = os.path.join(FACE_INFO_FOLDER, PICKLE_FILE)
    with open(pickle_file_path, 'wb') as picklefile:
        pickle.dump(face_info, picklefile)
        print('Wrote encodings pickled to {fname}'.format(fname=pickle_file_path))

regenerate_id_in_face_info()

print('Loaded {count} encodings'.format(count=len(face_info['encodings'])))