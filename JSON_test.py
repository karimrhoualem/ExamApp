from flask import Flask
from flask import jsonify
app = Flask(__name__)

@app.route('/')
def send():
    return jsonify(firstName='Karim', lastName='Rhoualem', ID=26603157)

# Run from command line using flask run --host="<IP Address>" --port=<portNumber>
if __name__ == "__main__":
    app.run()