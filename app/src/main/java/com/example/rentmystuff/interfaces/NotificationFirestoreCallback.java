package com.example.rentmystuff.interfaces;



import com.example.rentmystuff.classes.Notification;

import java.util.ArrayList;

public interface NotificationFirestoreCallback {
    void onCallback(ArrayList<Notification> list);

}
