package com.example.rentmystuff;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AppCompatActivity {
    private EditText email_etxt;
    private EditText password_etxt;
    private EditText confirm_password_etxt;
    private EditText fname_etxt;
    private EditText lname_etxt;

    private Button login_btn;
    private Button register_btn;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_etxt = findViewById(R.id.emailETxt);
        password_etxt = findViewById(R.id.passwordETxt);
        confirm_password_etxt = findViewById(R.id.confirmPasswordETxt);
        fname_etxt = findViewById(R.id.firstNameETxt);
        lname_etxt = findViewById(R.id.lastNameETxt);

        login_btn = findViewById(R.id.loginBtn);
        register_btn = findViewById(R.id.registerBtn);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_etxt.getText().toString();
                String password = password_etxt.getText().toString();
                String confirm_password = confirm_password_etxt.getText().toString();
                String fname = fname_etxt.getText().toString();
                String lname = lname_etxt.getText().toString();

                String[] arr = {email,password,confirm_password,fname,lname};
                for (int i = 0; i < arr.length ; i++) {
                    if (arr[i].length() ==0){
                        Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    }
                }
                if(!email.contains("@")){
                    Toast.makeText(RegisterActivity.this, "Email format is not correct", Toast.LENGTH_SHORT).show();
                }
                if(password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                }
                if(!password.equals(confirm_password)){
                    Toast.makeText(RegisterActivity.this, "password confirmation failed", Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < fname.length(); i++) {
                    if(!Character.isAlphabetic(fname.charAt(i))){
                        Toast.makeText(RegisterActivity.this, "first name contains illegal characters", Toast.LENGTH_SHORT).show();
                    }
                }
                for (int i = 0; i < lname.length(); i++) {
                    if(!Character.isAlphabetic(lname.charAt(i))){
                        Toast.makeText(RegisterActivity.this, "last name contains illegal characters", Toast.LENGTH_SHORT).show();
                    }
                }
                User new_user = new User(password,fname, lname);

                DocumentReference doc_ref = db.collection("users").document(email);
                doc_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "This email address is already in use!");
                                Toast.makeText(RegisterActivity.this, "This email address is already in use!", Toast.LENGTH_SHORT).show();
                            } else {
                                db.collection("users").document(email).set(new_user);
                                Log.d(TAG, "Registration completed successfully!");
                                Toast.makeText(RegisterActivity.this, "Registration completed successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,ActionTypeActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Log.d(TAG, "Failed with: ", task.getException());

                        }
                    }
                });
            }
        });


    }
}