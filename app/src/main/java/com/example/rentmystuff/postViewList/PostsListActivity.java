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

import com.example.rentmystuff.interfaces.PostsFirestoreCallback;
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
import java.util.Observable;
import java.util.Observer;

/**
 * This is the PostsListActivity class.
 * It can be reached from the "HomeActivity" and "ProfileActivity" pages.
 * From this page the user can reach the "PostPageActivity" page or go back to the "HomeActivity", "ProfileActivity" or "LoginActivity" pages.
 */

public class PostsListActivity extends AppCompatActivity implements Observer {
    private RecyclerView posts_rec_view; //this allows us to show a list dynamically.
    private SearchView posts_search_view;
    private PostsRecViewAdapter adapter; //adapts between the recycler view and the design of the items inside.

    private ArrayList<Post> posts;
    private PostListModel post_list_model;

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
                post_list_model.signOut();
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
        post_list_model = new PostListModel();
        post_list_model.addObserver(this);

        adapter = new PostsRecViewAdapter(PostsListActivity.this, posts);
        posts_rec_view.setAdapter(adapter);

        post_list_model.getPosts(email, posts, new PostsFirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Post> list) {
                adapter.setPosts(list);
            }
        });

        //if the email is empty, previous page was the "HomeActivity".
        //If so, show all posts in the database.

    }

    @Override
    public void update(Observable observable, Object o) {
        if(o instanceof String){
            Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}