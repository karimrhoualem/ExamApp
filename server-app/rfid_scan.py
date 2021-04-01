import argparse
import math
import os
import pickle
import threading
import multiprocessing
import time
import timeit

from flask import Flask
from flask import Response, jsonify
from flask import render_template


# TODO: try https://github.com/ondryaso/pi-rc522 instead for lower CPU usage?
import RPi.GPIO as GPIO
from mfrc522 import SimpleMFRC522
reader = SimpleMFRC522()

# CONFIG
IP_ADDRESS = "192.168.0.91"
PORT = 5001


# init
rfid_status = {'status':'noscan', 'name':'', 'id':''} # used to track most recent RFID scan info
app = Flask(__name__)


# Function to run in a thread that will constantly check the RFID scanner
def rfid_detect_loop():
    #global rfid_status
    while(True):
        try:
            id, text = reader.read()
            #print(id)
            #print(text)
            print("RFID: Detected card, UID:{uid}, data:{data}".format(uid=id, data=text))
            fields = text.split(',')
            
            if(len(fields) == 2):
                rfid_status['name'] = fields[0]
                rfid_status['id'] = fields[1].replace(' ', '')
                rfid_status['status'] = 'goodscan'
            else:
                rfid_status['status'] = 'badscan'
                rfid_status['name'] = ''
                rfid_status['id'] = ''
            
        except Exception as e:
            rfid_status['status'] = 'error'
            rfid_status['error'] = str(e)
            rfid_status['name'] = ''
            rfid_status['id'] = ''
            
        #finally:
         #  GPIO.cleanup()
            
        time.sleep(1) # don't check too often


@app.route("/rfid")
def rfid_status_route():
    return jsonify(rfid_status)



# check to see if this is the main thread of execution
if __name__ == '__main__':
    t = threading.Thread(target=rfid_detect_loop)
    t.daemon = True
    t.start()
    
    # start the flask app
    app.run(host=IP_ADDRESS, port=PORT, debug=True,
            threaded=True, use_reloader=False)
    

GPIO.cleanup()