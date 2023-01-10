package com.example.rentmystuff;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;

public class Model extends Observable {

    protected FirebaseFirestore db;
    protected FirebaseAuth auth;

    public Model() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void signOut() {
        auth.signOut();
    }



}
