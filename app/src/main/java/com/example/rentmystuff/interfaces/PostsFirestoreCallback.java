package com.example.rentmystuff.interfaces;

import com.example.rentmystuff.classes.Interested;
import com.example.rentmystuff.classes.Post;

import java.util.ArrayList;

public interface PostsFirestoreCallback {
    void onCallback(ArrayList<Post> list);

}
