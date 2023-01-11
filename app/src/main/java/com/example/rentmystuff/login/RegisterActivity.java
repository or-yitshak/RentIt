package com.example.rentmystuff.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rentmystuff.interfaces.FastApi;
import com.example.rentmystuff.HomeActivity;
import com.example.rentmystuff.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is the Register class.
 * This page can be reached from the "LoginActivity" page.
 * From this page the user can be sent back to the "LoginActivity" or the "HomeActivity" page.
 */

public class RegisterActivity extends AppCompatActivity implements Observer {

    private EditText email_etxt, password_etxt, confirm_password_etxt, fname_etxt, lname_etxt;
    private Button login_btn, register_btn;
    private ProgressDialog prog_dialog;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private LoginModel login_model;
    private FastApi fastApi;

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

        login_model = new LoginModel();
        login_model.addObserver(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.0.23:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        fastApi = retrofit.create(FastApi.class);

        //initializing the "Register" button once clicked:
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = email_etxt.getText().toString();
                String password = password_etxt.getText().toString();
                String confirm_password = confirm_password_etxt.getText().toString();
                String fname = fname_etxt.getText().toString();
                String lname = lname_etxt.getText().toString();
                createUser(email, password, confirm_password, fname, lname);
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

    private void createUser(String email, String password, String confirm_password, String first_name, String last_name) {
        prog_dialog.setMessage("Pleases Wait For Registration To Complete");
        prog_dialog.setTitle("Registration");
        prog_dialog.setCanceledOnTouchOutside(false);
        prog_dialog.show();

        CreateUser user = new CreateUser(email, password, confirm_password, first_name, last_name);
        Call<String> call =fastApi.createUser(user);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Code:"  + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(!response.body().equals("good")){
                        prog_dialog.dismiss();
                        Toast.makeText(RegisterActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    login_model.signIn(email, password);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        if(o.toString().equals("Successfully Login")){
            prog_dialog.dismiss();
            SendUserToNextActivity();
            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
        }
    }

    public class CreateUser{
        String email;
        String password;
        String confirm_password;
        String first_name;
        String last_name;

        public CreateUser(String email, String password, String confirm_password, String first_name, String last_name) {
            this.email = email;
            this.password = password;
            this.confirm_password = confirm_password;
            this.first_name = first_name;
            this.last_name = last_name;
        }
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