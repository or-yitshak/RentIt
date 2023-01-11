package com.example.rentmystuff.login;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rentmystuff.HomeActivity;
import com.example.rentmystuff.R;

import java.util.Observable;
import java.util.Observer;

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

public class LoginActivity extends AppCompatActivity implements Observer {
    private EditText email_etxt;
    private EditText password_etxt;
    private Button login_btn;
    private Button register_btn;

    private ProgressDialog prog_dialog; // small window during loading process.
    private LoginModel login_model;
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

        login_model = new LoginModel();
        login_model.addObserver(this);

        email_etxt = findViewById(R.id.emailETxt);
        password_etxt = findViewById(R.id.passwordETxt);
        login_btn = findViewById(R.id.loginBtn);
        register_btn = findViewById(R.id.registerBtn);

        prog_dialog = new ProgressDialog(this);

        //Initializing the login button:
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_etxt.getText().toString().trim();
                String password = password_etxt.getText().toString();
                //Checking that the email and password is properly inputted according to the constraints:
                prog_dialog.setMessage("Pleases Wait For Login To Complete");
                prog_dialog.setTitle("Login");
                prog_dialog.setCanceledOnTouchOutside(false); //false so the user won't ruin the process.
                prog_dialog.show();

                login_model.signIn(email, password);

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

    @Override
    public void update(Observable observable, Object message) {
        prog_dialog.dismiss();
        Toast.makeText(LoginActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
        if(message.toString().equals("Successfully Login")){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
    }
}
