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

/**
 * This is the PostActivity class.
 * It can be reached from the "Home" page.
 * Once the user clicks "Post it" he is sent back to the "HomeActivity" page.
 */

public class PostActivity extends AppCompatActivity {

    private ActivityPostBinding binding; //This allows us to reach all XML views, without re-initializing the variables.

    private Uri imageUri; //This contains the image content.
    private StorageReference storageReference; //reference to the firebase storage that contain the images uploaded.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    private String imageURL;
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
                auth.signOut();
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
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            post_id = extras.getString("id");
            binding.newPostTextView.setText("Edit Post");
            db.collection("posts").document(post_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Post post = documentSnapshot.toObject(Post.class);
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
            });
        }
        //getting the storage reference and creating an "image" directory inside.
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
                    Post new_post = new Post(auth.getCurrentUser().getEmail().toString(), category, title, description, imageURL, address, price, priceCategory);
                    if(checkInput(new_post)) {
                        db.collection("posts").add(new_post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                db.collection("posts").document(documentReference.getId()).update("post_id", documentReference.getId());
                            }
                        });
                        Toast.makeText(PostActivity.this, "Your post has been published", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(PostActivity.this, "Error, please make sure you enter all the details", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else{
                    if (imageURL != null) {
                        db.collection("posts").document(post_id).update("image_URL", imageURL);
                    }
                    if (!title.equals("")) {
                        db.collection("posts").document(post_id).update("title", title);
                    }
                    if (!address.equals("")) {
                        db.collection("posts").document(post_id).update("address", address);
                    }
                    if (!category.equals("")) {
                        db.collection("posts").document(post_id).update("category", category);
                    }
                    if (!price.equals("")) {
                        db.collection("posts").document(post_id).update("price", price);
                    }
                    if (!description.equals("")) {
                        db.collection("posts").document(post_id).update("description", description);
                    }
                    if(!category.equals("Category")){
                        db.collection("posts").document(post_id).update("category", category);
                    }
                    if(!priceCategory.equals("Currency")){
                        db.collection("posts").document(post_id).update("price_category", priceCategory);
                    }
                    Toast.makeText(PostActivity.this, "Your post has been updated", Toast.LENGTH_SHORT).show();

                }
                Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            private boolean checkInput(Post post) {
                if (post.getTitle().equals("") || post.getAddress().equals("") ||
                    post.getCategory().equals("Please select category") || post.getPrice().equals("") ||
                    post.getDescription().equals("") ) {
                    return false;
                }
                return true;
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
                Toast.makeText(PostActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
            binding.firebaseImage.setImageURI(imageUri);
        }
    }
}