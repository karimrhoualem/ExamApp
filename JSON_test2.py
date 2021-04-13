from flask import Flask
from flask import jsonify
app = Flask(__name__)

@app.route('/')
def send():
    return jsonify(name = 'Paulina Aviles', ID=40109825)

# Run from command line using flask run --host="<IP Address>" --port=<portNumber>
if __name__ == "__main__":
    app.run(host="192.168.0.166", port="5000")
