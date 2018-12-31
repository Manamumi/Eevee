import uuid

from flask import Blueprint, jsonify, render_template, request

from inside.inside_app.apps.application import InsideApplication
from inside.inside_app.inside import gitlab

apps_blueprint = Blueprint('apps', __name__, template_folder='templates')


@apps_blueprint.route('/')
def allAppsPage():
    return render_template('apps/app_list.html')


@apps_blueprint.route('/api/', methods=['GET'])
def getAllApplications():
    return jsonify({
        'apps': InsideApplication.objects
    })

@apps_blueprint.route('/api/', methods=['POST'])
def createNewApplication():
    me = gitlab.get('user').data

    return jsonify(
        InsideApplication(
            owner=me.get('name', None),
            ownerGitlabUsername=me['username'],
            name=request.form['name'],
            token=str(uuid.uuid4())
        ).save()
    )

@apps_blueprint.route('/api/', methods=['DELETE'])
def deleteApplication():
    InsideApplication.objects(token=request.form['token']).delete()
    return jsonify({
        'error': None
    })
