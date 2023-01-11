package com.example.rentmystuff.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.rentmystuff.classes.Interested;
import com.example.rentmystuff.classes.Post;
import com.example.rentmystuff.databinding.ActivityPostPageBinding;
import com.example.rentmystuff.login.LoginActivity;
import com.example.rentmystuff.classes.Notification;
import com.example.rentmystuff.notificationViewList.NotificationActivity;
import com.example.rentmystuff.postViewList.PostsListActivity;
import com.example.rentmystuff.profile.ProfileActivity;
import com.example.rentmystuff.profileViewList.ProfileListActivity;
import com.example.rentmystuff.R;
import com.example.rentmystuff.classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * This is the PostPageActivity class.
 * It can be reached from the "PostListActivity" page.
 * From this page the user can reach the "PostListActivity" or "InterestedActivity" pages.
 */

public class PostPageActivity extends AppCompatActivity {

    private String post_id; //id of the post.
    private Post curr_post; // the current post object.
    private DatePickerDialog date_picker; // allows the user to choose a date.
    private ActivityPostPageBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

//    private

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
                Intent intent = new Intent(PostPageActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(PostPageActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
                return true;
            case R.id.notificationBtn:
                Intent intent3 = new Intent(PostPageActivity.this, NotificationActivity.class);
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
        setContentView(R.layout.activity_post_page);

        getSupportActionBar().setTitle("PostPageActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityPostPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //initializing the date being picked:
        initDatePicker();

        //this receives information from where the post came from.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            post_id = extras.getString("id");
        }

        //extracting the wanted post from the database:
        db.collection("posts").document(post_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            //If post found in database, create a Post object and extract its information into the page:
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                curr_post = new Post();
                curr_post = documentSnapshot.toObject(Post.class);
                curr_post.setPost_id(post_id);

                binding.titleTxt.setText(curr_post.getTitle());
                binding.categoryTxt.setText("Category: " + curr_post.getCategory());
//                binding.publisherTxt.setText("Publisher Name: " + curr_post.getPublisher_email());
                binding.addressTxt.setText("Address: " + curr_post.getAddress());
                binding.priceTxt.setText("Price: " + curr_post.getPrice()+ curr_post.getPrice_category());
                binding.descriptionContentTxt.setText(curr_post.getDescription());

                //Using Picasso to download an image using a URL:
                Picasso.get()
                        .load(curr_post.getImageURL())
                        .fit()
                        .centerCrop()
                        .into(binding.imgView);

                //extracting the users information:
                db.collection("users").document(curr_post.getPublisher_email()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        binding.publisherTxt.setText("Publisher Name: " + user.getFirst_name() + " " + user.getLast_name());
                    }
                });

                //Checking if the post is the current users post. If so show different set of buttons:
                if (auth.getCurrentUser().getEmail().equals(curr_post.getPublisher_email())) {
                    binding.rentBtn.setVisibility(View.GONE);
                    binding.datesBtn.setVisibility(View.GONE);
                    binding.interestedBtn.setVisibility(View.VISIBLE);
                    binding.EditBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        //If the date button was clicked, open date picker:
        binding.datesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        // Creating a new "Interested" object and uploading it to the database as a sub-collection of the current post:
        // When the proccess is over, the user is sent to the "PostListActivity".
        binding.rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.datesBtn.getText().toString().equals("choose dates")){
                    Toast.makeText(PostPageActivity.this, "Please select date for rent", Toast.LENGTH_SHORT).show();
                    return;
                }

                Interested in = new Interested(auth.getCurrentUser().getEmail(), binding.datesBtn.getText().toString(), post_id);
                if(in.isDatePassed()){
                    Toast.makeText(PostPageActivity.this, "Please select legitimate date for rent", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.collection("posts")
                        .document(post_id)
                        .collection("interested").add(in).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                db.collection("posts")
                                        .document(post_id)
                                        .collection("interested")
                                        .document(documentReference.getId())
                                        .update("interested_id",documentReference.getId());
                                in.setInterested_id(documentReference.getId());
                                Notification notification = new Notification(post_id, false);
                                notification.setDate(binding.datesBtn.getText().toString());
                                notification.setInterested_id(in.getInterested_id());

                                db.collection("users")
                                        .document(curr_post.getPublisher_email())
                                        .collection("notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                db.collection("users")
                                                        .document(curr_post.getPublisher_email())
                                                        .collection("notifications")
                                                        .document(documentReference.getId())
                                                        .update("notification_id",documentReference.getId());

                                                db.collection("posts")
                                                        .document(post_id)
                                                        .collection("interested")
                                                        .document(in.getInterested_id())
                                                        .update("notification_id", documentReference.getId());
                                            }
                                        });
                            }
                        });

                Toast.makeText(PostPageActivity.this, "Your request has been submitted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PostPageActivity.this, PostsListActivity.class);
                startActivity(intent);
            }
        });

        //This button will appear only if it is the current users post.
        // This leads the user to the "InterestedActivity".
        binding.interestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostPageActivity.this, ProfileListActivity.class);
                intent.putExtra("id", post_id);
                startActivity(intent);
            }
        });
        binding.EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostPageActivity.this, PostActivity.class);
                intent.putExtra("id", post_id);
                startActivity(intent);
            }
        });
    }

    /**
     * This function determines what dates can be chosen for the user.
     */
    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                binding.datesBtn.setText(date);
            }
        };

        //creating a new calender:
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        //initializing the date picker to be the current days date.
        date_picker = new DatePickerDialog(this, style, dateSetListener, year, month, day);

    }


    /**
     * This function shows the date picked.
     */
    public void openDatePicker() {
        date_picker.show();
    }
}