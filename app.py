# note: to run on port other than 5000: https://stackoverflow.com/a/60209739/9421977
# and use 'flask run'

from flask import Flask
from datetime import datetime

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/time')
def show_time():
    return datetime.now().isoformat()


if __name__ == '__main__':
    app.run()
