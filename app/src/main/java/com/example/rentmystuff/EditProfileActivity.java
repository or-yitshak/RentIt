package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.rentmystuff.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * This is the EditProfileActivity class.
 * This can be reached and sends only to the "ProfileActivity" page.
 */

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    private Uri imageUri; // the URI of the profile image.
    private StorageReference storageReference; // reference to the storage in firebase.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    private String imageURL; // URL of the profile image.

    //Adding a menu-bar (UI) allowing the user to go to his profile or log out.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setTitle("EditProfileActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //logical code for the menu-bar:
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Profile:
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(EditProfileActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
                return true;
            case R.id.notificationBtn:
                Intent intent3 = new Intent(EditProfileActivity.this, NotificationActivity.class);
                startActivity(intent3);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setting the storage reference to the firebase storage images:
        storageReference = FirebaseStorage.getInstance().getReference("images");

        //allows user to choose image form the gallery:
        binding.selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        //allows user to upload image to the app:
        binding.uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        String email = auth.getCurrentUser().getEmail();
        db.collection("users").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                binding.firstNameETxt.setHint(user.getFirst_name());
                binding.lastNameETxt.setHint(user.getLast_name());
                binding.phoneNumber.setHint(user.getPhone_number());

                //Using Picasso library to download an image using URL:
                Picasso.get()
                        .load(user.getImage_URL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(binding.imgView);
            }
        });
        binding.verificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, VerificationActivity.class);
                startActivity(intent);
            }
        });
        //once user clicked, check all input is according to the constraints. If so, upload to the firestore:
        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String first_name = binding.firstNameETxt.getText().toString();
                String last_name = binding.lastNameETxt.getText().toString();
                String phone_number = binding.phoneNumber.getText().toString();
                String email = auth.getCurrentUser().getEmail();
                if (imageURL != null) {
                    db.collection("users").document(email).update("image_URL", imageURL);
                }
                if (!first_name.equals("") && checkInput(first_name)) {
                    db.collection("users").document(email).update("first_name", first_name);
                }
                if (!last_name.equals("") && checkInput(last_name)) {
                    db.collection("users").document(email).update("last_name", last_name);
                }
                if(!phone_number.equals("") && phone_number.length() <= 15){
                    db.collection("users").document(email).update("phone_number", phone_number);
                }
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * This function checks the user input according to the constraints.
     */
    private boolean checkInput(String st) {
        if (!st.matches("[a-zA-Z]+")) {
            return false;
        }
        return true;
    }

    /**
     * This function return a string of the extension of the file (image file).
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

        if (imageUri == null) {
            Toast.makeText(EditProfileActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
            return;
        }

        //Show progress window:
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        //Getting the firebase storage:
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        //Uploading the image to the firebase storage:
        UploadTask uploadTask = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri)).putFile(imageUri);

        //If upload is successful, the link will be copied to the imageURL variable:
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(EditProfileActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                imageURL = task.getResult().toString();
                            }
                        });
            }

        });
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
            binding.imgView.setImageURI(imageUri);
        }
    }
}