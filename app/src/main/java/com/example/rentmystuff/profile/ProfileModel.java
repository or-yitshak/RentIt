package com.example.rentmystuff.profile;

import com.example.rentmystuff.Model;
import com.example.rentmystuff.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileModel extends Model {

    public ProfileModel() {
        super();

    }

    public void signOut() {
        auth.signOut();
    }


    public String getEmail() {
        return auth.getCurrentUser().getEmail();
    }

    public void getUserInfo(String email) {
        db.collection("users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                setChanged();
                notifyObservers(user);
            }
        });
    }

    public void updateUserInfo(String first_name, String last_name, String phone_number) {

        String email = auth.getCurrentUser().getEmail();
        if (imageURL != null) {
            db.collection("users").document(email).update("image_URL", imageURL);
        }
        if (!first_name.equals("") && checkInput(first_name)) {
            db.collection("users").document(email).update("first_name", first_name);
        }
        if (!last_name.equals("") && checkInput(last_name)) {
            db.collection("users").document(email).update("last_name", last_name);
        }
        if(!phone_number.equals("") && phone_number.length() <= 15){
            db.collection("users").document(email).update("phone_number", phone_number);
        }
    }

    /**
     * This function checks the user input according to the constraints.
     */
    private boolean checkInput(String st) {
        if (!st.matches("[a-zA-Z]+")) {
            return false;
        }
        return true;
    }
}
