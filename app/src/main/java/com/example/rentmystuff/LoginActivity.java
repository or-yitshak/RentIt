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
 * This is the Login class.
 * It is the first page (onCreate) of the application.
 * From this page the user can be sent to the "RegisterActivity" or the "HomeActivity" page.
 * It has four variables:
 * 1) email_etxt - user edit email text.
 * 2) password_etxt - user password edit text.
 * 3) login_btn - user login button.
 * 4) register_btn - user register button. When clicked, user is sent to the registration page.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText email_etxt;
    private EditText password_etxt;
    private Button login_btn;
    private Button register_btn;

    private ProgressDialog prog_dialog; // small window during loading process.
    private FirebaseAuth auth; // uses to register and login new users.
    private FirebaseUser fire_user; //created by authentication (auth).
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //the firebase database.

    /**
     * This function exits the app when pressed back.
     */
    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    /**
     * This function takes care of user login and registration when the app is opened.
     */
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

        //Initializing the login button:
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });


        //Initializing the registration button:
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent moves the user from page "Login" to page "Register".
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * This function allows the user to login.
     */
    private void login() {
        String email = email_etxt.getText().toString().trim();
        String password = password_etxt.getText().toString();
        //Checking that the email and password is properly inputted according to the constraints:
        if (inputChecks(email, password)) {
            //Showing the dialog window to the user:
            prog_dialog.setMessage("Pleases Wait For Login To Complete");
            prog_dialog.setTitle("Login");
            prog_dialog.setCanceledOnTouchOutside(false); //false so the user won't ruin the process.
            prog_dialog.show();

            //using firebase authentication for the user login process:
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //if login is successful send to home page:
                    if (task.isSuccessful()) {
                        prog_dialog.dismiss();
                        SendUserToNextActivity(); //sends to home page
                        Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                    } else {
                        prog_dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Incorrect password or email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * This function checks if the input is acceptable according to the constraints.
     */
    private boolean inputChecks(String email, String password) {
        String[] arr = {email, password};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length() == 0) {
                Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //email letter restriction.
        String email_pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!email.matches(email_pattern)) {
            Toast.makeText(LoginActivity.this, "Email format is not correct", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * This function sends the user to the home page.
     */
    private void SendUserToNextActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
