package com.example.rentmystuff;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginModel extends Model {

    public LoginModel() {
        super();
    }

    public void signIn(String email, String password){

        setChanged();
        if(inputChecks(email, password)) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //if login is successful send to home page:
                    if (task.isSuccessful()) {
                        notifyObservers("Successfully Login");
                    } else {
                        notifyObservers("Incorrect password or email");
                    }
                }
            });
        }
    }

    /**
     * This function checks if the input is acceptable according to the constraints.
     */
    private boolean inputChecks(String email, String password) {
        String[] arr = {email, password};
        for (String s : arr) {
            if (s.length() == 0) {
                notifyObservers("Please fill all the fields");
                return false;
            }
        }
        //email letter restriction.
        String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(email_pattern)) {
            notifyObservers("Email format is not correct");
            return false;
        }
        return true;
    }

}
