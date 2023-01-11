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
import java.util.Observable;
import java.util.Observer;

/**
 * This is the PostPageActivity class.
 * It can be reached from the "PostListActivity" page.
 * From this page the user can reach the "PostListActivity" or "InterestedActivity" pages.
 */

public class PostPageActivity extends AppCompatActivity implements Observer {

    private String post_id; //id of the post.
    private Post curr_post; // the current post object.
    private DatePickerDialog date_picker; // allows the user to choose a date.
    private ActivityPostPageBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private PostModel post_model;

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

        post_model = new PostModel();
        post_model.addObserver(this);

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
        post_model.getPostInfo(post_id);

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
                post_model.addInterested(in, curr_post);


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

    @Override
    public void update(Observable observable, Object o) {

        if(o instanceof Post){

            curr_post = (Post) o;
            if (post_model.getAuthEmail().equals(curr_post.getPublisher_email())) {
                binding.rentBtn.setVisibility(View.GONE);
                binding.datesBtn.setVisibility(View.GONE);
                binding.interestedBtn.setVisibility(View.VISIBLE);
                binding.EditBtn.setVisibility(View.VISIBLE);
            }

            binding.titleTxt.setText(curr_post.getTitle());
            binding.categoryTxt.setText("Category: " + curr_post.getCategory());
            binding.addressTxt.setText("Address: " + curr_post.getAddress());
            binding.priceTxt.setText("Price: " + curr_post.getPrice()+ curr_post.getPrice_category());
            binding.descriptionContentTxt.setText(curr_post.getDescription());

            //Using Picasso to download an image using a URL:
            Picasso.get()
                    .load(curr_post.getImageURL())
                    .fit()
                    .centerCrop()
                    .into(binding.imgView);
        }

        else if(o instanceof User){
            User user = (User) o;
            binding.publisherTxt.setText("Publisher Name: " + user.getFirst_name() + " " + user.getLast_name());

        }
        else if( o instanceof String){
            Toast.makeText(PostPageActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
        }
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