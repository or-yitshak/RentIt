package com.example.rentmystuff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EditPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        //checking if the previous activity sent extra data:
        Bundle extras = getIntent().getExtras();
        String post_id;
        if (extras != null) {
            post_id = extras.getString("id");
        }
    }
}