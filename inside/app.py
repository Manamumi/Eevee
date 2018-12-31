#!/usr/bin/python3.7
from inside.inside_app.inside import app
from inside.inside_app.routes import *

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=7744)
