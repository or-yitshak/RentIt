package com.example.rentmystuff;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class ViewHolderModel extends Model{
    ViewHolderModel(){
        super();
    }

    public void setOnBindViewHolder(Interested curr_inter) {
        db.collection("users").document(curr_inter.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User curr_user = documentSnapshot.toObject(User.class);
                setChanged();
                notifyObservers(curr_user);

            }
        });
    }

    public void updateApproved(Interested curr_inter) {
        db.collection("posts").document(curr_inter.getPost_id())
                .collection("interested")
                .document(curr_inter.getInterested_id())
                .update("approved",true);
    }
}
