import os

import grpc
from mongoengine import connect

from proto.coffee.coffee_pb2_grpc import CoffeeStub

from .inside import app

if app.debug:
    connect('eevee')
else:
    connect('eevee', host='mongo.eevee.xyz')

_coffee_channel = grpc.insecure_channel('coffee.eevee.xyz:7755')
coffee_client = CoffeeStub(_coffee_channel)
inside_app_token = [
    ('x-inside-token', os.environ['INSIDE_APP_TOKEN'])
]