package com.example.rentmystuff;

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

import com.example.rentmystuff.databinding.ActivityPostPageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class PostPageActivity extends AppCompatActivity {
    private String post_id;
    private Post curr_post;

    private DatePickerDialog date_picker;

    private ActivityPostPageBinding binding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    private FirebaseAuth auth = FirebaseAuth.getInstance();

    //Menu Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.Profile:
                Intent intent = new Intent(PostPageActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(PostPageActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        getSupportActionBar().setTitle("PostPageActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityPostPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDatePicker();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            post_id = extras.getString("id");
        }


        db.collection("posts").document(post_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                curr_post = new Post();
                curr_post = documentSnapshot.toObject(Post.class);
                curr_post.setPost_id(post_id);

                binding.titleTxt.setText(curr_post.getTitle());
                binding.categoryTxt.setText("Category: " + curr_post.getCategory());
//                binding.publisherTxt.setText("Publisher Name: " + curr_post.getPublisher_email());
                binding.addressTxt.setText("Address: " + curr_post.getAddress());
                binding.priceTxt.setText("Price: " + curr_post.getPrice());
                binding.descriptionContentTxt.setText(curr_post.getDescription());
                Picasso.get()
                        .load(curr_post.getImageURL())
                        .fit()
                        .centerCrop()
                        .into(binding.imgView);

                db.collection("users").document(curr_post.getPublisher_email()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        binding.publisherTxt.setText("Publisher Name: " + user.getFirst_name() + " " + user.getLast_name());
                    }
                });

                if(auth.getCurrentUser().getEmail().equals(curr_post.getPublisher_email())){
                    binding.rentBtn.setVisibility(View.GONE);
                    binding.datesBtn.setVisibility(View.GONE);
                    binding.interestedBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.datesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });

        binding.rentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Interested in = new Interested(auth.getCurrentUser().getEmail(), binding.datesBtn.getText().toString());
                db.collection("posts")
                        .document(post_id)
                        .collection("interested").add(in);
                Intent intent = new Intent(PostPageActivity.this, PostsListActivity.class);
                startActivity(intent);
            }
        });

        binding.interestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostPageActivity.this, InterestedActivity.class);
                intent.putExtra("id", post_id);
                startActivity(intent);
            }
        });


    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                binding.datesBtn.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        date_picker = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
    public void openDatePicker()
    {
        date_picker.show();
    }
}