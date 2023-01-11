package com.example.rentmystuff.postViewList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.rentmystuff.notificationViewList.NotificationActivity;
import com.example.rentmystuff.R;
import com.example.rentmystuff.login.LoginActivity;
import com.example.rentmystuff.classes.Post;
import com.example.rentmystuff.profile.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the PostsListActivity class.
 * It can be reached from the "HomeActivity" and "ProfileActivity" pages.
 * From this page the user can reach the "PostPageActivity" page or go back to the "HomeActivity", "ProfileActivity" or "LoginActivity" pages.
 */

public class PostsListActivity extends AppCompatActivity {
    private RecyclerView posts_rec_view; //this allows us to show a list dynamically.
    private SearchView posts_search_view;
    private PostsRecViewAdapter adapter; //adapts between the recycler view and the design of the items inside.

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Post> posts;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private String email = "";

    //Adding a menu-bar (UI) allowing the user to go to his profile or log out.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //logical code for the menu-bar:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Profile:
                Intent intent = new Intent(PostsListActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(PostsListActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
                return true;
            case R.id.notificationBtn:
                Intent intent3 = new Intent(PostsListActivity.this, NotificationActivity.class);
                startActivity(intent3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is the onCreate function.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        getSupportActionBar().setTitle("PostsListActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //checking if the previous activity sent extra data:
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }

        posts_rec_view = findViewById(R.id.postsRecView);
        posts_search_view = findViewById(R.id.postsSearchView);
        posts_search_view.clearFocus();
        posts_search_view.setImeOptions(EditorInfo.IME_ACTION_DONE);
        posts_search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        posts_rec_view.setLayoutManager(new GridLayoutManager(this, 2));
        posts = new ArrayList<>();

        //if the email is empty, previous page was the "HomeActivity".
        //If so, show all posts in the database.
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
                        adapter = new PostsRecViewAdapter(PostsListActivity.this, posts, "PostsListActivity");
                        posts_rec_view.setAdapter(adapter);
                    } else {
                        Toast.makeText(PostsListActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
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
                        adapter = new PostsRecViewAdapter(PostsListActivity.this, posts, "MyProfileActivity");
                        posts_rec_view.setAdapter(adapter);
                    } else {
                        Toast.makeText(PostsListActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}