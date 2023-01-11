package com.example.rentmystuff;

import com.example.rentmystuff.classes.Interested;
import com.example.rentmystuff.classes.Notification;
import com.example.rentmystuff.classes.Post;
import com.example.rentmystuff.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

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

    public void setNotificationOnBindViewHolder(Notification curr_noti) {
//        final String[] publisher_email = new String[1];
        db.collection("posts").document(curr_noti.getPost_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                Post post = doc.toObject(Post.class);
                String post_id = post.getPost_id();
                String publisher_email = post.getPublisher_email();
                if(!auth.getCurrentUser().getEmail().equals(publisher_email)) {//if its not my post
                    //Using Picasso library to download an image using URL:
                    setChanged();
                    notifyObservers(post);

                    db.collection("users").document(publisher_email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User curr_user = documentSnapshot.toObject(User.class);
                            String approved = "";
                            if (curr_noti.isApproved()) {
                                approved = "approved";
                            }
                            else {
                                approved = "declined";
                            }
                            String[] ans = {curr_user.getFirst_name() + " " + curr_user.getLast_name() + " has " + approved + " your request","Email: " + publisher_email};
                            setChanged();
                            notifyObservers(ans);
                        }
                    });
                }
                else{
                    db.collection("posts").document(post_id).collection("interested").document(curr_noti.getInterested_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Interested interested = documentSnapshot.toObject(Interested.class);
                            db.collection("users").document(interested.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User inter = documentSnapshot.toObject(User.class);
                                    setChanged();
                                    notifyObservers(inter);
                                    String[] ans ={inter.getFirst_name() + " " + inter.getLast_name() + " wants to rent your " + post.getTitle(),"Email: " + interested.getEmail()};
                                    setChanged();
                                    notifyObservers(ans);
                                }
                            });

                        }
                    });
                }

            }
        });
    }
}
