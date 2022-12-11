package com.example.rentmystuff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.rentmystuff.databinding.ActivityMyProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/**
 * This is the ProfileActivity class.
 * Once user is logged in, this page can be reached from everywhere through the menu-bar.
 * From this page the user can reach "PostsListActivity" and "EditProfileActivity" pages.
 */

public class ProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String email = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("MyProfileActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        } else {
            email = auth.getCurrentUser().getEmail();
        }

        //Check if the user is on his own page. If so, show the edit post button.
        if (auth.getCurrentUser().getEmail().equals(email)) {
            binding.EditBtn.setVisibility(View.VISIBLE);
        }

        //Send the user to his own post-list:
        binding.postsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PostsListActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        //Sends to "EditProfileActivity":
        binding.EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        //extract from the firestore database the user information:
        db.collection("users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                binding.firstNameTxt.setText("First Name: " + user.getFirst_name());
                binding.lastNameTxt.setText("Last Name: " + user.getLast_name());
                binding.emailTxt.setText("Email: " + email);

                //Using Picasso library to download an image using URL:
                Picasso.get()
                        .load(user.getImage_URL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(binding.imgView);
            }
        });
    }
}