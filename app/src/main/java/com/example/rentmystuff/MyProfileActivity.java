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

public class MyProfileActivity extends AppCompatActivity {
    private ActivityMyProfileBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("MyProfileActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.postsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, MyPostsListActivity.class);
                startActivity(intent);            }
        });

        binding.EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);            }
        });

        db.collection("users").document(auth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                binding.firstNameTxt.setText("First Name: " + user.getFirst_name());
                binding.lastNameTxt.setText("Last Name: " + user.getLast_name());
                binding.emailTxt.setText("Email: " + auth.getCurrentUser().getEmail());
                Picasso.get()
                        .load(user.getImage_URL())
                        .fit()
                        .centerCrop()
                        .into(binding.imgView);
            }
        });


    }
}