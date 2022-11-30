package com.example.rentmystuff;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private EditText email_etxt;
    private EditText password_etxt;
    private Button login_btn;
    private Button register_btn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_etxt = findViewById(R.id.emailETxt);
        password_etxt = findViewById(R.id.passwordETxt);
        login_btn = findViewById(R.id.loginBtn);
        register_btn = findViewById(R.id.registerBtn);

        //login button action
        String email = email_etxt.getText().toString();
        String password = password_etxt.getText().toString();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DocumentReference doc_ref = db.collection("users").document(email);
                    doc_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(TAG, "email exists!");
                                    String registered_password = document.get("password").toString();
                                    if (registered_password.equals(password)) {
                                        Intent intent = new Intent(MainActivity.this, ActionTypeActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Wrong password ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "This email address is not registered!");
                                    Toast.makeText(MainActivity.this, "This email address is not registered", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                                Toast.makeText(MainActivity.this, "An error has occurred please try again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //register button action
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


    }
}
