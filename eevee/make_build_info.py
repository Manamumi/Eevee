import datetime
import json
import os

CI_COMMIT_SHA = os.environ.get('CI_COMMIT_SHA', 'Dummy SHA').strip()
CI_COMMIT_MESSAGE = os.environ.get('CI_COMMIT_MESSAGE', 'Dummy Message').strip()
CI_JOB_ID = os.environ.get('CI_PIPELINE_IID', 'Dummy Job ID').strip()
GITLAB_USER_LOGIN = os.environ.get('GITLAB_USER_LOGIN', 'Dummy User').strip()
GITLAB_USER_ID = os.environ.get('GITLAB_USER_ID', '1').strip()
GITLAB_USER_NAME = os.environ.get('GITLAB_USER_NAME', 'Dummy Name').strip()
BUILD_TIME = str(datetime.datetime.utcnow())

current_dir = os.path.abspath(os.path.dirname(__file__))

print(json.dumps(
    {
        'ciCommitSHA': CI_COMMIT_SHA,
        'ciCommitMessage': CI_COMMIT_MESSAGE,
        'ciJobID': CI_JOB_ID,
        'gitlabUserLogin': GITLAB_USER_LOGIN,
        'gitlabUserID': GITLAB_USER_ID,
        'gitlabUserName': GITLAB_USER_NAME,
        'buildTime': BUILD_TIME
    }
))