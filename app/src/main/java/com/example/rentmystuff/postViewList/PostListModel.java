package com.example.rentmystuff.postViewList;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.rentmystuff.Model;
import com.example.rentmystuff.classes.Post;
import com.example.rentmystuff.interfaces.PostsFirestoreCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PostListModel extends Model {

    PostListModel(){
        super();
    }

    public void getPosts(String email, ArrayList<Post> posts, PostsFirestoreCallback postsFirestoreCallback) {
        if (email.equals("")) {
            db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //if task is successful, loop over all the posts and insert them in the array.
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Post curr_post = doc.toObject(Post.class);
                            curr_post.setPost_id(doc.getId());
                            posts.add(curr_post);
                        }
                        postsFirestoreCallback.onCallback(posts);
                    } else {
                        setChanged();
                        notifyObservers("An error has occurred");
                    }
                }
            });
        }
        //If email is not empty, previous page was from a Profile.
        //If so, show all the posts of the current profile user:
        else {
            db.collection("posts").whereEqualTo("publisher_email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    //if task is successful, loop over all the posts and insert them in the array.
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Post curr_post = doc.toObject(Post.class);
                            curr_post.setPost_id(doc.getId());
                            posts.add(curr_post);
                        }
                        postsFirestoreCallback.onCallback(posts);
                    } else {
                        setChanged();
                        notifyObservers("An error has occurred");                    }
                }
            });
        }
    }
}
