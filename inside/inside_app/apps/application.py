from mongoengine import BooleanField, Document, StringField

class InsideApplication(Document):
    owner = StringField(required=False, default=None)
    ownerGitlabUsername = StringField(required=True)
    name = StringField(required=True)
    token = StringField(required=True)
    isActive = BooleanField(required=True, default=True)