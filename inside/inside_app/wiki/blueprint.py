from flask import Blueprint, render_template

wiki_blueprint = Blueprint('wiki', __name__, template_folder='templates')


@wiki_blueprint.route('/', defaults={'black_hole': ''})
@wiki_blueprint.route('/<path:black_hole>')
def wildcard_get(black_hole):
    return render_template('wiki/wiki.html')