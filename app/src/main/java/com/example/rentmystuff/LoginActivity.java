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

public class LoginActivity extends AppCompatActivity {
    private EditText email_etxt;
    private EditText password_etxt;
    private Button login_btn;
    private Button register_btn;

    ProgressDialog prog_dialog;
    private FirebaseAuth auth;
    private FirebaseUser fire_user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_etxt = findViewById(R.id.emailETxt);
        password_etxt = findViewById(R.id.passwordETxt);
        prog_dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        fire_user = auth.getCurrentUser();

        login_btn = findViewById(R.id.loginBtn);
        register_btn = findViewById(R.id.registerBtn);

        //login button action
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


        //register button action
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    private void login() {
        String email = email_etxt.getText().toString();
        String password = password_etxt.getText().toString();
        if (inputChecks(email, password)) {
            prog_dialog.setMessage("Pleases Wait For Login To Complete");
            prog_dialog.setTitle("Login");
            prog_dialog.setCanceledOnTouchOutside(false);
            prog_dialog.show();

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        prog_dialog.dismiss();
                        SendUserToNextActivity();
                        Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                    } else {
                        prog_dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Incorrect password or email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean inputChecks(String email, String password) {
        String[] arr = {email, password};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() == 0) {
                Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!email.matches(email_pattern)) {
            Toast.makeText(LoginActivity.this, "Email format is not correct", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void SendUserToNextActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
