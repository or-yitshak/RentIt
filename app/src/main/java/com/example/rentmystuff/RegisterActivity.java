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

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText email_etxt, password_etxt, confirm_password_etxt, fname_etxt, lname_etxt;
    private Button login_btn, register_btn;
    private ProgressDialog prog_dialog;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    ;
    private FirebaseUser fire_user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        fire_user = auth.getCurrentUser();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.164:8000")
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

                //Checking that the input is properly inputted according to the constraints:
//                if (inputChecks(email, password, confirm_password, fname, lname)) {
//                    //Showing the dialog window to the user:
//                    prog_dialog.setMessage("Pleases Wait For Registration To Complete");
//                    prog_dialog.setTitle("Registration");
//                    prog_dialog.setCanceledOnTouchOutside(false);
//                    prog_dialog.show();
//
//                    //creating a new user if the user is not found in the firebase authentication:
//                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            //If user was created, initialize user object and insert to the "Users" collection in firestore.
//                            if (task.isSuccessful()) {
//                                User new_user = new User(fname, lname);
//                                //inserting to firestore.
//                                db.collection("users").document(email).set(new_user);
//                                prog_dialog.dismiss();
//                                SendUserToNextActivity();
//                                Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
//                            } else {
//                                prog_dialog.dismiss();
//                                Toast.makeText(RegisterActivity.this, "Failed to Register" + task.getException(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
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

        CreateUser user = new CreateUser(email, password, confirm_password, first_name, first_name);
        Call<CreateUser> call =fastApi.createUser(user);
        call.enqueue(new Callback<CreateUser>() {
            @Override
            public void onResponse(Call<CreateUser> call, Response<CreateUser> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Code:"  + response.code(), Toast.LENGTH_SHORT).show();
                    fname_etxt.setText(response.errorBody().toString());
                    return;
                }else{
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            prog_dialog.dismiss();
                            SendUserToNextActivity();
                            Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CreateUser> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                fname_etxt.setText(t.getMessage());
            }
        });

//        User user = new User("abc@gmail.com","111111","lll","054616161");
//        Call<User> call =fastApi.createUser2(user);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (!response.isSuccessful()) {
//                    Toast.makeText(RegisterActivity.this, "Code:"  + response.code(), Toast.LENGTH_SHORT).show();
//                    fname_etxt.setText(response.errorBody().toString());
//                    return;
//                }else{
//                    SendUserToNextActivity();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
////                fname_etxt.setText(t.getMessage());
//            }
//        });
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