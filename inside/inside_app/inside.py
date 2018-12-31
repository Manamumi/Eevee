import os

from flask import Flask, session
from flask_mongoengine import MongoEngine
from flask_oauthlib.client import OAuth
from flask_wtf.csrf import CSRFProtect
from werkzeug.contrib.fixers import ProxyFix

DUMMY_SECRET_KEY = 'DUMMY_DO_NOT_USE_IN_PROD'
REAL_SECRET_KEY = os.environ.get('INSIDE_SECRET_KEY', DUMMY_SECRET_KEY)
IS_PROD = os.environ.get('RUNTIME_ENV', None) == 'PROD' or REAL_SECRET_KEY != DUMMY_SECRET_KEY
IS_DUMMY = os.environ.get('RUNTIME_ENV', None) == 'DUMMY'

# Bazel places our runfiles whereever CWD + project is
# apparently for pars. We can take advantage of this
# by using CWD + "inside" as the root for resources in
# both bazel and docker.
ROOT_DIR = os.path.abspath(os.path.join(os.getcwd(), 'inside'))

app = Flask(
    __name__,
    template_folder=os.path.join(ROOT_DIR, 'templates'),
    static_folder=os.path.join(ROOT_DIR, 'static')
)

app.config['SESSION_COOKIE_NAME'] = 'inside_session'
app.config['MONGODB_SETTINGS'] = {
    'db': 'eevee',
}

if not IS_PROD or IS_DUMMY:
    app.debug = True
else:
    app.wsgi_app = ProxyFix(app.wsgi_app)
    # TODO: Use Coffee.
    app.config['SERVER_NAME'] = 'inside.eevee.xyz'
    app.config['PREFERRED_URL_SCHEME'] = 'https'
    app.config['SESSION_COOKIE_DOMAIN'] = '.eevee.xyz'
    app.config['SESSION_COOKIE_SECURE'] = True
    app.config['MONGODB_SETTINGS']['host'] = 'mongo-1.storage.sf2.machines.eevee.xyz'
    csrf = CSRFProtect(app)

app.config['SECRET_KEY'] = REAL_SECRET_KEY

oauth = OAuth(app)
mongo = MongoEngine(app)

# TODO: Use Coffee for some of this stuff too!!!
gitlab = oauth.remote_app(
    'Git @ Eevee',
    consumer_key='7406c2d9a98781754b183ce0c993859e964a0f08257dd3219c5d67b2b5b39774',
    consumer_secret='d1d504af6c61c79a0b88e7c6c04644e7d3bb059d0be8a89a8bb56e56fe6b8461',
    request_token_params={'scope': 'api'},
    base_url='<redacted>/api/v4/',
    request_token_url=None,
    access_token_method='POST',
    access_token_url='<redacted>/oauth/token',
    authorize_url='<redacted>/oauth/authorize'
)


@app.context_processor
def injections():
    me = gitlab.get('user').data

    injections = {
        'gitlabUsername': me['username'],
        'gitlabDisplayName': me['name'],
        'gitlabAvatar': me['avatar_url']
    }

    if app.debug:
        injections['csrf_token'] = lambda: 0

    return injections


@gitlab.tokengetter
def get_gitlab_oauth_token():
    return session.get('gitlab_token')