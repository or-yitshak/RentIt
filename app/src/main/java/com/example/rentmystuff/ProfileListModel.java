package com.example.rentmystuff;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileListModel extends Model{

    ProfileListModel(){
        super();
    }



    public void initInterestedList(String post_id, ArrayList<Interested> interested_list, FirestoreCallback firestore_callback) {
        db.collection("posts").document(post_id).collection("interested").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //If task is successful, insert interested user to the list:
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Interested curr_inter = doc.toObject(Interested.class);
                        if(curr_inter.isDatePassed()){
                            db.collection("posts")
                                    .document(curr_inter.getPost_id())
                                    .collection("interested")
                                    .document(curr_inter.getInterested_id()).delete();
                        }
                        else{
                            interested_list.add(curr_inter);
                        }
                    }
                    firestore_callback.onCallback(interested_list);
                } else {
                    setChanged();
                    notifyObservers("An error has occurred");
                }

            }
        });
    }




    public void setOnBindViewHolder(int position) {
    }

    public void deleteInterested(Interested curr_inter, Context context) {
        db.collection("posts")
                .document(curr_inter.getPost_id())
                .collection("interested")
                .document(curr_inter.getInterested_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "the request has been removed!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void addNotification(Interested curr_inter, boolean flag) {
        Notification notification = new Notification(curr_inter.getPost_id(), flag);
        notification.setDate(curr_inter.getDate());
        db.collection("users")
                .document(curr_inter.getEmail())
                .collection("notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        db.collection("users")
                                .document(curr_inter.getEmail())
                                .collection("notifications")
                                .document(documentReference.getId())
                                .update("notification_id",documentReference.getId());
                    }
                });
    }
}
