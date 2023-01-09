package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.rentmystuff.databinding.ActivityPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Observable;
import java.util.Observer;

/**
 * This is the PostActivity class.
 * It can be reached from the "Home" page.
 * Once the user clicks "Post it" he is sent back to the "HomeActivity" page.
 */

public class PostActivity extends AppCompatActivity implements Observer {

    private ActivityPostBinding binding; //This allows us to reach all XML views, without re-initializing the variables.

    private Uri imageUri; //This contains the image content.
//    private StorageReference storageReference; //reference to the firebase storage that contain the images uploaded.
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
//
//    private String imageURL;
    private PostModel post_model;
    String post_id = "";

    //Adding a menu-bar (UI) allowing the user to go to his profile or log out.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setTitle("PostActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //logical code for the menu-bar:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //checking here what button was clicked:
        switch (item.getItemId()) {
            //send to the "ProfileActivity" page:
            case R.id.Profile:
                Intent intent = new Intent(PostActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            //send to the "LoginActivity" page and log user out of account:
            case R.id.logOutBtn:
                Intent intent2 = new Intent(PostActivity.this, LoginActivity.class);
                post_model.signOut();
                startActivity(intent2);
                return true;
            case R.id.notificationBtn:
                Intent intent3 = new Intent(PostActivity.this, NotificationActivity.class);
                startActivity(intent3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This is the onCreate function.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initializing binding (instead of initializing the XML page variables):
        post_model = new PostModel();
        post_model.addObserver(this);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            post_id = extras.getString("id");
            binding.newPostTextView.setText("Edit Post");
            post_model.setHint(post_id);
        }
        //getting the storage reference and creating an "image" directory inside.

        binding.selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        binding.uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        //Initialing the "Post-it" button. On click, extract the post information into the database:
        binding.postItBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String category = binding.categorySpinner.getSelectedItem().toString();
                String title = binding.titleEditText.getText().toString();
                String address = binding.addressEditText.getText().toString();
                String price = binding.priceEditText.getText().toString();
                String description = binding.descriptionEditText.getText().toString();
                String priceCategory = binding.priceSpinner.getSelectedItem().toString();
                //checking if the previous activity sent extra data:

                if(post_id == "") {
                    post_model.postIt(category, title, description, address, price, priceCategory);
                } else{
                    post_model.editPost(category, title, description, address, price, priceCategory,post_id);
                }
            }
        });

    }

    /**
     * This function checks the type of URI inserted.
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * This function allows the user to upload an image to the app.
     * It checks the imageUri and uploads it to the firebase storage.
     * If the check fails, a message is displayed to the user asking him to select image.
     * Otherwise the function uploads the image to the firebase storage and saves the link as a variable.
     */
    private void uploadImage() {
        //Check if imageUri is null:
        if (imageUri == null) {
            Toast.makeText(PostActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
            return;
        }
        //Show progress window:
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();
        String image_name = System.currentTimeMillis() + "." + getFileExtension(imageUri);
        //Uploading the image to the firebase storage:
        post_model.uploadImage(imageUri, image_name);

    }

    /**
     * This function allows the user to choose an image from his gallery.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    /**
     * This function stores the data inside the imageUri and displays it on the screen.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null) {

            imageUri = data.getData();
            binding.firebaseImage.setImageURI(imageUri);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        if(o instanceof String){
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Toast.makeText(PostActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
            if(o.toString().equals("Your post has been published") || o.toString().equals("Your post has been updated") ){
                Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
        else {
            Post post = (Post) o;
            binding.titleEditText.setHint(post.getTitle());
            binding.addressEditText.setHint(post.getAddress());
            binding.priceEditText.setHint(post.getPrice());
            binding.descriptionEditText.setHint(post.getDescription());
            Picasso.get()
                    .load(post.getImageURL())
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    .into(binding.firebaseImage);
        }

    }
}