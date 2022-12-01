package com.example.rentmystuff;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_etxt, password_etxt, confirm_password_etxt, fname_etxt, lname_etxt;
    private Button login_btn, register_btn;
    ProgressDialog prog_dialog;
    private FirebaseAuth auth =FirebaseAuth.getInstance();;
    private FirebaseUser fire_user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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
        prog_dialog = new ProgressDialog(this);
        fire_user = auth.getCurrentUser();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_etxt.getText().toString();
                String password = password_etxt.getText().toString();
                String confirm_password = confirm_password_etxt.getText().toString();
                String fname = fname_etxt.getText().toString();
                String lname = lname_etxt.getText().toString();


                if (inputChecks(email, password, confirm_password, fname, lname)) {
                    prog_dialog.setMessage("Pleases Wait For Registration To Complete");
                    prog_dialog.setTitle("Registration");
                    prog_dialog.setCanceledOnTouchOutside(false);
                    prog_dialog.show();

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User new_user = new User(fname, lname);
                                db.collection("users").document(email).set(new_user);
                                prog_dialog.dismiss();
                                SendUserToNextActivity();
                                Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            } else {
                                prog_dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Failed to Register" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

//                User new_user = new User(fname, lname);
//
//                DocumentReference doc_ref = db.collection("users").document(email);
//                doc_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                Log.d(TAG, "This email address is already in use!");
//                                Toast.makeText(RegisterActivity.this, "This email address is already in use!", Toast.LENGTH_SHORT).show();
//                            } else {
//                                db.collection("users").document(email).set(new_user);
//                                Log.d(TAG, "Registration completed successfully!");
//                                Toast.makeText(RegisterActivity.this, "Registration completed successfully!", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(RegisterActivity.this,ActionTypeActivity.class);
//                                startActivity(intent);
//                            }
//                        } else {
//                            Log.d(TAG, "Failed with: ", task.getException());
//
//                        }
//                    }
//                });
            }
        });


    }

    private boolean inputChecks(String email, String password, String confirm_password, String first_name, String last_name) {
        String[] arr = {email, password, confirm_password, first_name, last_name};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() == 0) {
                Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!email.matches(email_pattern)) {
            Toast.makeText(RegisterActivity.this, "Email format is not correct", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6) {
            Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirm_password)) {
            Toast.makeText(RegisterActivity.this, "Password confirmation failed", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!last_name.matches("[a-zA-Z]+") || !first_name.matches("[a-zA-Z]+")) {
            Toast.makeText(RegisterActivity.this, "Full name contains illegal characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void SendUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this, ActionTypeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}