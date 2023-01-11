from fastapi import FastAPI
import firebase_admin
from firebase_admin import credentials, firestore, auth
from firebase_admin.exceptions import FirebaseError
from pydantic import BaseModel
import re


cred = credentials.Certificate("rent-it-key.json")
firebase_admin.initialize_app(cred)


class User(BaseModel):
    email: str
    password: str
    confirm_password: str
    first_name: str
    last_name: str
    # image_URL: str
    # phone_number: str

app = FastAPI()


@app.post("/create-user/")
async def create_user(user: User):
    is_good = input_checks(user)
    if is_good != "good":
        return is_good
    try:
        auth.create_user(email=user.email,password= user.password)
    except ValueError:
        return "This email address is already in use"
    except FirebaseError:
        return "Error occured while creating user"
    db = firestore.client()  # connecting to firestore
    data = {
        u'first_name': user.first_name,
        u'last_name': user.last_name,
        u'image_URL': None,
        u'phone_number': None
    }
    lc_email = user.email.lower()
    db.collection(u'users').document(lc_email).set(data)
    return is_good

def input_checks(user: User):
    user.email = user.email.strip()
    user.first_name = user.first_name.strip()
    user.last_name = user.last_name.strip()
    arr = [user.email, user.password, user.confirm_password, user.first_name, user.last_name]
    for i in range(len(arr)):
        if len(arr[i]) == 0:
            return "Please fill all the fields"
    email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    if not re.match(email_pattern, user.email):
        return "Email format is not correct"
    elif len(user.password) < 6:
        return "Password is too short"
    elif user.password != user.confirm_password:
        return "Password confirmation failed"
    elif not user.last_name.isalpha() or not user.first_name.isalpha():
        return "Full name contains illegal characters"
    return "good"
