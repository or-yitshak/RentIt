package com.example.rentmystuff.post;

import android.view.View;
import android.widget.Toast;

import com.example.rentmystuff.Model;
import com.example.rentmystuff.classes.Interested;
import com.example.rentmystuff.classes.Notification;
import com.example.rentmystuff.classes.Post;
import com.example.rentmystuff.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PostModel extends Model {

    public PostModel() {
        super();
//        storageReference = FirebaseStorage.getInstance().getReference().child("images");
    }

    public void postIt(String category, String title, String description, String address, int price, String priceCategory) {
        setChanged();
        Post new_post = new Post(auth.getCurrentUser().getEmail().toString(), category, title, description, imageURL, address, price, priceCategory);
        if(checkInput(new_post)) {
            db.collection("posts").add(new_post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    db.collection("posts").document(documentReference.getId()).update("post_id", documentReference.getId());
                }
            });
            notifyObservers("Your post has been published");
        } else{
            notifyObservers("Error, please make sure you enter all the details");
        }
    }

    private boolean checkInput(Post post) {
        if (post.getTitle().equals("") || post.getAddress().equals("") ||
                post.getCategory().equals("Category") || post.getPrice() == 0 ||
                post.getDescription().equals("") || post.getPrice_category().equals("Currency")) {
            setChanged();
            notifyObservers("Error make sure you enter all the details");
            return false;
        }
        return true;
    }

    public void editPost(String category, String title, String description, String address, int price, String priceCategory, String post_id) {
        if (imageURL != null) {
            db.collection("posts").document(post_id).update("imageURL", imageURL);
        }
        if (!title.equals("")) {
            db.collection("posts").document(post_id).update("title", title);
        }
        if (!address.equals("")) {
            db.collection("posts").document(post_id).update("address", address);
        }
        if (price != 0) {
            db.collection("posts").document(post_id).update("price", price);
        }
        if (!description.equals("")) {
            db.collection("posts").document(post_id).update("description", description);
        }
        if(!category.equals("Category")){
            db.collection("posts").document(post_id).update("category", category);
        }
        if(!priceCategory.equals("Currency")){
            db.collection("posts").document(post_id).update("price_category", priceCategory);
        }
        setChanged();
        notifyObservers("Your post has been updated");
    }

    public void setHint(String post_id) {
        db.collection("posts").document(post_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post post = documentSnapshot.toObject(Post.class);
                setChanged();
                notifyObservers(post);
            }
        });
    }

    public void getPostInfo(String post_id) {

        db.collection("posts").document(post_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            //If post found in database, create a Post object and extract its information into the page:
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Post curr_post = documentSnapshot.toObject(Post.class);
                setChanged();
                notifyObservers(curr_post);

                //extracting the users information:
                db.collection("users").document(curr_post.getPublisher_email()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        setChanged();
                        notifyObservers(user);
                    }
                });
            }
        });
    }

    public void addInterested(Interested in, Post curr_post) {

        if(in.isDatePassed()){
            setChanged();
            notifyObservers("Please select legitimate date for rent");
            return;
        }

        String post_id = curr_post.getPost_id();
        db.collection("posts")
                .document(post_id)
                .collection("interested").add(in).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        db.collection("posts")
                                .document(post_id)
                                .collection("interested")
                                .document(documentReference.getId())
                                .update("interested_id",documentReference.getId());
                        in.setInterested_id(documentReference.getId());
                        Notification notification = new Notification(post_id, false);
                        notification.setDate(in.getDate());
                        notification.setInterested_id(in.getInterested_id());

                        db.collection("users")
                                .document(curr_post.getPublisher_email())
                                .collection("notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        db.collection("users")
                                                .document(curr_post.getPublisher_email())
                                                .collection("notifications")
                                                .document(documentReference.getId())
                                                .update("notification_id",documentReference.getId());

                                        db.collection("posts")
                                                .document(post_id)
                                                .collection("interested")
                                                .document(in.getInterested_id())
                                                .update("notification_id", documentReference.getId());
                                    }
                                });
                        setChanged();
                        notifyObservers("Your request has been submitted");
                    }
                });
    }
}
