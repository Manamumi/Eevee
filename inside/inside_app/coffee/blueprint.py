from flask import Blueprint, jsonify, render_template, request
import re

from proto.coffee.coffee_pb2 import EntryType, Key, StringEntry, NumberEntry, BooleanEntry, StringListEntry
from inside.inside_app.common import coffee_client, inside_app_token

coffee_blueprint = Blueprint('coffee', __name__, template_folder='templates')


def _unmarshall_list(l, parse):
    l = l[1:-1].split(',')
    return [parse(x.strip()) for x in l]


def _entry_to_json(entry):
    value_unmarshaller = {
        # String
        0: str,
        # Number
        1: float,
        # Boolean
        2: bool,
        # StringList
        3: lambda x: _unmarshall_list(x, str)
    }

    return {
        'key': '.'.join(entry.key),
        'children': {
            key: _entry_to_json(child)
            for key, child in entry.children.items()
        },
        'entryType': EntryType.Name(entry.entryType),
        'value': value_unmarshaller[entry.entryType](entry.value.encode('utf-8'))
    }


@coffee_blueprint.route('/api/', defaults={'route': ''}, methods=['GET'])
@coffee_blueprint.route('/api/<path:route>', methods=['GET'])
def api_get(route):
    key = Key(value=route.split('/')) if route else Key(value=None)
    return jsonify(_entry_to_json(
        coffee_client.Get(key, metadata=inside_app_token)
    ))


@coffee_blueprint.route('/api/', methods=['POST'])
def api_post():
    if request.form.get('key', '') == '':
        return jsonify({
            'error': 'Entry key must be specified'
        }), 400

    key = re.sub(r'^root\.?', '', request.form['key'])

    if request.form.get('value', None) is None:
        return jsonify({
            'error': 'Entry value must be specified.'
        }), 400

    if request.form.get('entryType', None) is None:
        return jsonify({
            'error': 'Entry type must be specified.'
        }), 400

    if request.form['entryType'] == 'String':
        coffee_client.SetString(StringEntry(
            key=key.split('.'),
            value=request.form['value']
        ), metadata=inside_app_token)
    elif request.form['entryType'] == 'Number':
        coffee_client.SetNumber(NumberEntry(
            key=key.split('.'),
            value=float(request.form['value'])
        ), metadata=inside_app_token)
    elif request.form['entryType'] == 'Boolean':
        coffee_client.SetBoolean(BooleanEntry(
            key=key.split('.'),
            value=bool(request.form['value'])
        ), metadata=inside_app_token)
    elif request.form['entryType'] == 'StringList':
        coffee_client.SetStringList(StringListEntry(
            key=key.split('.'),
            value=re.compile(r'\r?\n').split(request.form['value'])
        ), metadata=inside_app_token)
    else:
        return jsonify({
            'error': 'Unknown entry type provided.'
        }), 400

    return jsonify({
        'error': None,
        'status': 'OK'
    })


@coffee_blueprint.route('/api/', methods=['DELETE'])
def api_delete():
    if request.form.get('key', '') == '':
        return jsonify({
            'error': 'Entry key must be specified'
        }), 400

    key = re.sub(r'^root\.?', '', request.form['key'])

    if key == '':
        return jsonify({
            'error': 'Root node may not be deleted.'
        }), 400

    coffee_client.Delete(Key(value=key.split('.')), metadata=inside_app_token)

    return jsonify({
        'error': None,
        'status': 'OK'
    })


@coffee_blueprint.route('/', defaults={'black_hole': ''})
@coffee_blueprint.route('/<path:black_hole>')
def wildcard_get(black_hole):
    return render_template('coffee/coffee.html')
