package com.example.rentmystuff;

import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Observable;

public class PostModel extends Model {

    public PostModel() {
        super();
//        storageReference = FirebaseStorage.getInstance().getReference().child("images");
    }

    public void postIt(String category, String title, String description, String address, String price, String priceCategory) {
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
                post.getCategory().equals("Category") || post.getPrice().equals("") ||
                post.getDescription().equals("") || post.getPrice_category().equals("Currency")) {
            setChanged();
            notifyObservers("Error make sure you enter all the details");
            return false;
        }
        return true;
    }

    public void editPost(String category, String title, String description, String address, String price, String priceCategory, String post_id) {
        if (imageURL != null) {
            db.collection("posts").document(post_id).update("imageURL", imageURL);
        }
        if (!title.equals("")) {
            db.collection("posts").document(post_id).update("title", title);
        }
        if (!address.equals("")) {
            db.collection("posts").document(post_id).update("address", address);
        }
        if (!price.equals("")) {
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
}
