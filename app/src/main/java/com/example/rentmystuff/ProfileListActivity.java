package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This is the ProfileListActivity class.
 * It can be reached from the "PostPageActivity" page.
 * From this page the user can reach "ProfileActivity" page.
 */

public class ProfileListActivity extends AppCompatActivity {

    private String post_id; // The id of the post.
    private RecyclerView interested_rec_view; // recycle view of the interested users.
    private ProfileRecViewAdapter adapter; //adapts between the recycler view and the design of the items inside.

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Interested> interested_list; // A list representing the interested users.
    private FirebaseAuth auth = FirebaseAuth.getInstance();

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
                auth.signOut();
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

        //extracting from the firestore database the interested users information:
        db.collection("posts").document(post_id).collection("interested").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //If task is successful, insert interested user to the list:
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Interested curr_inter = doc.toObject(Interested.class);
                        if(curr_inter.isDatePassed()){
                            interested_list.remove(curr_inter);
                            db.collection("posts")
                                    .document(curr_inter.getPost_id())
                                    .collection("interested")
                                    .document(curr_inter.getInterested_id()).delete();
                        }
                        else{
                            interested_list.add(curr_inter);
                        }
                    }
                    adapter = new ProfileRecViewAdapter(ProfileListActivity.this, interested_list, "MyProfileActivity");
                    interested_rec_view.setAdapter(adapter);
                    //If not, throw an error to the user:
                } else {
                    Toast.makeText(ProfileListActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
