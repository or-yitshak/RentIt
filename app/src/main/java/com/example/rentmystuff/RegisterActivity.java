package com.example.rentmystuff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private EditText email_etxt;
    private EditText password_etxt;
    private EditText confirm_password_etxt;
    private EditText fname_etxt;
    private EditText lname_etxt;
    private Button login_btn;
    private Button register_btn;

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
                User new_user = new User(email,password,fname, lname);
            }
        });


    }
}