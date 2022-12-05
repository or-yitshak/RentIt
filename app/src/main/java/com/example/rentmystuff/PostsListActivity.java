package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.rentmystuff.databinding.ActivityPostsListBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostsListActivity extends AppCompatActivity {
    private RecyclerView posts_rec_view;
    private PostsRecViewAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Post> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        posts_rec_view = findViewById(R.id.postsRecView);
        posts_rec_view.setLayoutManager(new GridLayoutManager(this, 2));
        posts = new ArrayList<>();

        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Post curr_post = doc.toObject(Post.class);
                        posts.add(curr_post);
                    }
                    adapter = new PostsRecViewAdapter(PostsListActivity.this,posts);
                    posts_rec_view.setAdapter(adapter);
                }else{
                    Toast.makeText(PostsListActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}