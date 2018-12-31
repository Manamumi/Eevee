import json

from flask import Blueprint, jsonify, render_template, request
import requests

ingest_blueprint = Blueprint('ingest', __name__, template_folder='templates')


@ingest_blueprint.route('/')
def ingest_overview():
    return render_template('ingest/overview.html')

@ingest_blueprint.route('/api/queues')
def get_queues():
    return jsonify(json.loads(requests.get('http://ingest.eevee.xyz:15672/api/queues/', auth=(
        'eevee', 'eevee'
    )).text))