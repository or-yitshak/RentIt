package com.example.rentmystuff;

import com.example.rentmystuff.classes.Interested;
import com.example.rentmystuff.classes.Post;
import com.example.rentmystuff.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

public class ViewHolderModel extends Model{
    public ViewHolderModel(){
        super();
    }

    public void setProfileOnBindViewHolder(Interested curr_inter) {
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

    public void setPostOnBindViewHolder(Post current_post) {
        db.collection("users").document(current_post.getPublisher_email()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User curr_user = documentSnapshot.toObject(User.class);
                setChanged();
                notifyObservers(curr_user);
            }
        });
    }
}
