package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActionTypeActivity extends AppCompatActivity {

    private TextView hello_txt;

    private Button post_btn;
    private Button rent_btn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth =FirebaseAuth.getInstance();;
    private FirebaseUser fire_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_type);

        fire_user = auth.getCurrentUser();
        post_btn = findViewById(R.id.postBtn);
        rent_btn = findViewById(R.id.rentBtn);
        hello_txt = findViewById(R.id.helloTextView);

        db.collection("users").document(fire_user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                hello_txt.setText("Hello "+user.getFirst_name()+"!");
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionTypeActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        rent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActionTypeActivity.this, PostsListActivity.class);
                startActivity(intent);
            }
        });



    }
}