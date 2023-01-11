package com.example.rentmystuff.profileViewList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.rentmystuff.interfaces.FirestoreCallback;
import com.example.rentmystuff.HomeActivity;
import com.example.rentmystuff.notificationViewList.NotificationActivity;
import com.example.rentmystuff.R;
import com.example.rentmystuff.classes.Interested;
import com.example.rentmystuff.login.LoginActivity;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * This is the ProfileListActivity class.
 * It can be reached from the "PostPageActivity" page.
 * From this page the user can reach "ProfileActivity" page.
 */

public class ProfileListActivity extends AppCompatActivity implements Observer {

    private String post_id; // The id of the post.
    private RecyclerView interested_rec_view; // recycle view of the interested users.
    private ProfileRecViewAdapter adapter; //adapts between the recycler view and the design of the items inside.

//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Interested> interested_list; // A list representing the interested users.
//    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProfileListModel profile_list_model;
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
                Intent intent = new Intent(ProfileListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(ProfileListActivity.this, LoginActivity.class);
                profile_list_model.signOut();
                startActivity(intent2);
                return true;
            case R.id.notificationBtn:
                Intent intent3 = new Intent(ProfileListActivity.this, NotificationActivity.class);
                startActivity(intent3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);

        getSupportActionBar().setTitle("ProfileListActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //checking if the previous activity sent extra data:
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            post_id = extras.getString("id");
        }

        //initializing the recycler view and the interested list:
        interested_rec_view = findViewById(R.id.interestedRecView);
        interested_rec_view.setLayoutManager(new LinearLayoutManager(this));
        interested_list = new ArrayList<Interested>();
        profile_list_model = new ProfileListModel();
        profile_list_model.addObserver(this);

        adapter = new ProfileRecViewAdapter(ProfileListActivity.this,interested_list, "MyProfileActivity", profile_list_model);
        interested_rec_view.setAdapter(adapter);

        profile_list_model.initInterestedList(post_id,interested_list, new FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Interested> list) {
                adapter.setInterested_list(list);
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();

    }
}
