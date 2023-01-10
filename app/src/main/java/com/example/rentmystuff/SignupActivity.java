//package com.example.rentmystuff;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.TextView;
//
//import com.example.rentmystuff.R;
//
//public class SignupActivity extends AppCompatActivity {
//    TextView alreadyhaveanaccount;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
//
//        alreadyhaveanaccount = findViewById(R.id.alreadyhaveanaccount);
//        alreadyhaveanaccount.setOnClickListener(v->{
//            startActivity(new Intent(SignupActivity.this, LoginActivityNew.class));
//        });
//
//    }
//}