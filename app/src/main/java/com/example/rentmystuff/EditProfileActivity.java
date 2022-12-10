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
import com.example.rentmystuff.databinding.ActivityPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {


    private ActivityEditProfileBinding binding;

    private Uri imageUri;
    private StorageReference storageReference;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    private String imageURL;

    //Menu Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setTitle("EditProfileActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.Profile:
                Intent intent = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(EditProfileActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference("images");

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

        binding.finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String first_name = binding.firstNameETxt.getText().toString();
                String last_name = binding.lastNameETxt.getText().toString();
                if(imageURL != null){
                    db.collection("users").document(auth.getCurrentUser().getEmail()).update("image_URL", imageURL);
                }
                if(first_name != "" && checkInput(first_name)) {
                    db.collection("users").document(auth.getCurrentUser().getEmail()).update("first_name", first_name);
                }
                if(last_name != "" && checkInput(last_name)) {
                    db.collection("users").document(auth.getCurrentUser().getEmail()).update("last_name", last_name);
                }
                Intent intent = new Intent(EditProfileActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkInput(String st){
        if (!st.matches("[a-zA-Z]+")) {
            Toast.makeText(EditProfileActivity.this, "Full name contains illegal characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {

        if (imageUri == null) {
            Toast.makeText(EditProfileActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading File....");
        progressDialog.show();

        StorageReference file_ref = storageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri));


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        UploadTask uploadTask = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri)).putFile(imageUri);
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

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null) {

            imageUri = data.getData();
            binding.imgView.setImageURI(imageUri);


        }
    }

}