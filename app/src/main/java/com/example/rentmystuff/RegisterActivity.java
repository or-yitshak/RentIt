package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is the Register class.
 * This page can be reached from the "LoginActivity" page.
 * From this page the user can be sent back to the "LoginActivity" or the "HomeActivity" page.
 */

public class RegisterActivity extends AppCompatActivity {

    private EditText email_etxt, password_etxt, confirm_password_etxt, fname_etxt, lname_etxt;
    private Button login_btn, register_btn;
    private ProgressDialog prog_dialog;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    ;
    private FirebaseUser fire_user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * This is the onCreate function.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("RegisterActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing the class variables:
        email_etxt = findViewById(R.id.emailETxt);
        password_etxt = findViewById(R.id.passwordETxt);
        confirm_password_etxt = findViewById(R.id.confirmPasswordETxt);
        fname_etxt = findViewById(R.id.firstNameETxt);
        lname_etxt = findViewById(R.id.lastNameETxt);

        login_btn = findViewById(R.id.loginBtn);
        register_btn = findViewById(R.id.registerBtn);
        prog_dialog = new ProgressDialog(this);
        fire_user = auth.getCurrentUser();

        //initializing the "Register" button once clicked:
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_etxt.getText().toString();
                String password = password_etxt.getText().toString();
                String confirm_password = confirm_password_etxt.getText().toString();
                String fname = fname_etxt.getText().toString();
                String lname = lname_etxt.getText().toString();

                //Checking that the input is properly inputted according to the constraints:
                if (inputChecks(email, password, confirm_password, fname, lname)) {
                    //Showing the dialog window to the user:
                    prog_dialog.setMessage("Pleases Wait For Registration To Complete");
                    prog_dialog.setTitle("Registration");
                    prog_dialog.setCanceledOnTouchOutside(false);
                    prog_dialog.show();

                    //creating a new user if the user is not found in the firebase authentication:
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //If user was created, initialize user object and insert to the "Users" collection in firestore.
                            if (task.isSuccessful()) {
                                User new_user = new User(fname, lname);
                                //inserting to firestore.
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
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This function checks if the input is acceptable according to the constraints.
     */
    private boolean inputChecks(String email, String password, String confirm_password, String first_name, String last_name) {
        String[] arr = {email, password, confirm_password, first_name, last_name};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() == 0) {
                Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
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

    /**
     * This function sends the user to the home page.
     */
    private void SendUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}