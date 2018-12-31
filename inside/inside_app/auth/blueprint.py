from flask import Blueprint, make_response, url_for, redirect, request, session

from inside.inside_app.apps.application import InsideApplication
from inside.inside_app.inside import gitlab

auth_blueprint = Blueprint('auth', __name__, template_folder='templates')


@auth_blueprint.route('/login')
def login():
    if 'gitlab_token' in session:
        return redirect(url_for('index'))
    return gitlab.authorize(
        callback=url_for(
            'auth.authorized',
            redirect=request.args.get('redirect', None),
            _external=True
        )
    )


@auth_blueprint.route('/logout')
def logout():
    session.pop('gitlab_token', None)
    return redirect(url_for('index'))


@auth_blueprint.route('/authorized')
def authorized():
    resp = gitlab.authorized_response()
    if resp is None or resp.get('access_token') is None:
        return 'Access denied: reason=%s error=%s resp=%s' % (
            request.args['error'],
            request.args['error_description'],
            resp
        )

    session['gitlab_token'] = (resp['access_token'], '')

    redirect_url = request.args.get('redirect', None)

    if redirect_url is not None:
        return redirect(redirect_url)

    return redirect(url_for('auth.login'))


@auth_blueprint.route('/check')
def sso_auth():
    app_token = request.args.get('appToken', None) or request.headers.get('X-Inside-Token', None)

    if app_token is not None:
        inside_app = InsideApplication.objects(token=app_token)

        if len(inside_app):
            resp = make_response('App Token Bypass')
            resp.headers['X-Forwarded-User'] = inside_app[0].name
            return resp, 200

    if 'gitlab_token' in session:
        me = gitlab.get('user').data
        resp = make_response('OK')
        resp.headers['X-Forwarded-User'] = me['username']
        resp.headers['X-Forwarded-User-Email'] = me['email']
        return resp, 200
    return 'Not Logged In', 401
