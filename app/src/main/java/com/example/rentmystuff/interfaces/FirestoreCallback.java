package com.example.rentmystuff.interfaces;

import com.example.rentmystuff.classes.Interested;

import java.util.ArrayList;

public interface FirestoreCallback {
    void onCallback(ArrayList<Interested> list);

}
