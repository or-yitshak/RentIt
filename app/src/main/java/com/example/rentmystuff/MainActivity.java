package com.example.rentmystuff;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private EditText email_etxt;
    private EditText password_etxt;
    private Button login_btn;
    private Button register_btn;

    ProgressDialog prog_dialog;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email_etxt = findViewById(R.id.emailETxt);
        password_etxt = findViewById(R.id.passwordETxt);
        prog_dialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        login_btn = findViewById(R.id.loginBtn);
        register_btn = findViewById(R.id.registerBtn);

        //login button action
        String email = email_etxt.getText().toString();
        String password = password_etxt.getText().toString();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
//                try {
//                    DocumentReference doc_ref = db.collection("users").document(email);
//                    doc_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                DocumentSnapshot document = task.getResult();
//                                if (document.exists()) {
//                                    Log.d(TAG, "email exists!");
//                                    String registered_password = document.get("password").toString();
//                                    if (registered_password.equals(password)) {
//                                        Intent intent = new Intent(MainActivity.this, ActionTypeActivity.class);
//                                        startActivity(intent);
//                                    } else {
//                                        Toast.makeText(MainActivity.this, "Wrong password ", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    Log.d(TAG, "This email address is not registered!");
//                                    Toast.makeText(MainActivity.this, "This email address is not registered", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Log.d(TAG, "Failed with: ", task.getException());
//                                Toast.makeText(MainActivity.this, "An error has occurred please try again", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
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

    private void login() {
        String email = email_etxt.getText().toString();
        String password = password_etxt.getText().toString();


        String[] arr = {email, password};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() == 0) {
                Toast.makeText(MainActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        }
        if (!email.matches(email_pattern)) {
            Toast.makeText(MainActivity.this, "Email format is not correct", Toast.LENGTH_SHORT).show();
        } else {
            prog_dialog.setMessage("Pleases Wait For Login To Complete");
            prog_dialog.setTitle("Login");
            prog_dialog.setCanceledOnTouchOutside(false);
            prog_dialog.show();

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        prog_dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Successfully Login", Toast.LENGTH_SHORT);
                        SendUserToNextActivity();
                    }
                    else{
                        prog_dialog.dismiss();
                        Log.d(TAG, "Incorrect password and email");
                        Toast.makeText(MainActivity.this, "Incorrect password and email"+task.getException(), Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }
    private void SendUserToNextActivity() {
        Intent intent = new Intent(MainActivity.this,ActionTypeActivity.class);
        startActivity(intent);
    }
}
