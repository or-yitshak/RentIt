package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rentmystuff.databinding.ActivityMyProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/**
 * This is the ProfileActivity class.
 * Once user is logged in, this page can be reached from everywhere through the menu-bar.
 * From this page the user can reach "PostsListActivity" and "EditProfileActivity" pages.
 */

public class ProfileActivity extends AppCompatActivity {

    private ActivityMyProfileBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String email = "";
    private String phone_number = "";
    private static final int REQUEST_SMS = 1;

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
                auth.signOut();
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

        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        } else {
            email = auth.getCurrentUser().getEmail();
        }

        //Check if the user is on his own page. If so, show the edit post button.
        if (auth.getCurrentUser().getEmail().equals(email)) {
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
        db.collection("users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
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
        });

        binding.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = "972" + phone_number; // Replace with phone number of the person you want to send a message to
                String message = ""; // Replace with the message you want to send

                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + message);
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                sendIntent.setPackage("com.whatsapp");
                startActivity(sendIntent);
            }
        });
    }

//    private boolean isInstalled(String url) {
//        PackageManager package_manager = getPackageManager();
//        boolean app_installed;
//        try{
//            package_manager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
//            app_installed = true;
//        } catch (PackageManager.NameNotFoundException e) {
////            e.printStackTrace();
//            app_installed = false;
//        }
//        return app_installed;
//    }
    private void requestSmsPermission() {
        String permission = Manifest.permission.SEND_SMS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{permission};
            ActivityCompat.requestPermissions(this, permissions, REQUEST_SMS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}