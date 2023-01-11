package com.example.rentmystuff.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rentmystuff.HomeActivity;
import com.example.rentmystuff.databinding.ActivityMyProfileBinding;
import com.example.rentmystuff.login.LoginActivity;
import com.example.rentmystuff.notificationViewList.NotificationActivity;
import com.example.rentmystuff.postViewList.PostsListActivity;
import com.example.rentmystuff.R;
import com.example.rentmystuff.classes.User;
import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

/**
 * This is the ProfileActivity class.
 * Once user is logged in, this page can be reached from everywhere through the menu-bar.
 * From this page the user can reach "PostsListActivity" and "EditProfileActivity" pages.
 */

public class ProfileActivity extends AppCompatActivity implements Observer {

    private ActivityMyProfileBinding binding;
    private ProfileModel profile_model;
    private String email = "";
    private String phone_number = "";

    //Adding a menu-bar (UI) allowing the user to go to his profile or log out.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //logical code for the menu-bar:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Profile:
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(ProfileActivity.this, LoginActivity.class);
                profile_model.signOut();
                startActivity(intent2);
                return true;
            case R.id.notificationBtn:
                Intent intent3 = new Intent(ProfileActivity.this, NotificationActivity.class);
                startActivity(intent3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("ProfileActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_model = new ProfileModel();
        profile_model.addObserver(this);

        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        } else {
            email = profile_model.getEmail();
        }

        //Check if the user is on his own page. If so, show the edit post button.
        if (profile_model.getEmail().equals(email)) {
            binding.EditBtn.setVisibility(View.VISIBLE);
            binding.messageBtn.setVisibility(View.INVISIBLE);
        }

        //Send the user to his own post-list:
        binding.postsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PostsListActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        //Sends to "EditProfileActivity":
        binding.EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        //extract from the firestore database the user information:
        profile_model.getUserInfo(email);

        binding.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    String phoneNumber = "972" + phone_number; // Replace with phone number of the person you want to send a message to
                    String message = ""; // Replace with the message you want to send

                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message);
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "app not installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void update(Observable observable, Object o) {
        User user = (User) o;
        binding.firstNameTxt.setText("First Name: " + user.getFirst_name());
        binding.lastNameTxt.setText("Last Name: " + user.getLast_name());
        binding.emailTxt.setText("Email: " + email);
        phone_number = user.getPhone_number();
        //Using Picasso library to download an image using URL:
        Picasso.get()
                .load(user.getImage_URL())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(binding.imgView);
    }
}