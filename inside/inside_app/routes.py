from flask import redirect, render_template, request, session, url_for

from .inside import app
from .apps.application import InsideApplication
from .apps.blueprint import apps_blueprint
from .auth.blueprint import auth_blueprint
from .coffee.blueprint import coffee_blueprint
from .wiki.blueprint import wiki_blueprint
from .ingest.blueprint import ingest_blueprint

app.register_blueprint(apps_blueprint, url_prefix='/apps')
app.register_blueprint(auth_blueprint, url_prefix='/auth')
app.register_blueprint(coffee_blueprint, url_prefix='/coffee')
app.register_blueprint(wiki_blueprint, url_prefix='/wiki')
app.register_blueprint(ingest_blueprint, url_prefix='/ingest')

@app.before_request
def ensure_logged_in():
    if request.path.startswith('/auth/'):
        return

    if request.args.get('appToken', None) is not None:
        if len(InsideApplication.objects(token=request.args.get('appToken'))):
            return

    if 'gitlab_token' not in session:
        return redirect(url_for('auth.login', redirect=request.args.get('redirect', None)))


@app.route('/')
def index():
    return render_template('inside.html')