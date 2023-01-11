package com.example.rentmystuff.notificationViewList;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rentmystuff.Model;
import com.example.rentmystuff.classes.Notification;
import com.example.rentmystuff.interfaces.NotificationFirestoreCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Observer;

public class NotificationModel extends Model {

    public NotificationModel() {
        super();
    }

    public void getNotifications(ArrayList<Notification> notification_list, NotificationFirestoreCallback notificationFirestoreCallback) {
        //extracting from the firestore database the notification information:
        db.collection("users").document(auth.getCurrentUser().getEmail()).collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //If task is successful, insert interested user to the list:
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Notification notification = doc.toObject(Notification.class);
                        notification_list.add(notification);
                    }
                    notificationFirestoreCallback.onCallback(notification_list);
                    //If not, throw an error to the user:
                } else {
                    setChanged();
                    notifyObservers("An error has occurred");
                }
            }
        });
    }
}
