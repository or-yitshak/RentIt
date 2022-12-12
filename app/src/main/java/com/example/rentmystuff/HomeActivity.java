package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is the Home class.
 * It can be reached from the "Register" or "Login" page.
 * From this page the user can be sent to the "PostsListActivity", "PostActivity", "ProfileActivity" or "Login" pages.
 */

public class HomeActivity extends AppCompatActivity {

    private TextView hello_txt;

    private Button post_btn;
    private Button rent_btn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    ;
    private FirebaseUser fire_user;

    @Override
    public void onBackPressed() {
    }

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
        //checking here what button was clicked:
        switch (item.getItemId()) {
            //send to the "ProfileActivity" page:
            case R.id.Profile:
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            //send to the "LoginActivity" page and log user out of account:
            case R.id.logOutBtn:
                Intent intent2 = new Intent(HomeActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
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
        setContentView(R.layout.activity_home);

        fire_user = auth.getCurrentUser();
        post_btn = findViewById(R.id.postBtn);
        rent_btn = findViewById(R.id.rentBtn);
        hello_txt = findViewById(R.id.helloTextView);

        //extracting the user information from firestore using the current user login information:
        db.collection("users").document(fire_user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                hello_txt.setText("Hello " + user.getFirst_name() + "!");
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        rent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, PostsListActivity.class);
                startActivity(intent);
            }
        });
    }
}