package com.example.rentmystuff;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentmystuff.databinding.ActivityPostBinding;
import com.example.rentmystuff.databinding.ActivityPostPageBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PostPageActivity extends AppCompatActivity {
    private String post_id;
    private Post curr_post;

//    private TextView title_txt;

    private ActivityPostPageBinding binding;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        binding = ActivityPostPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            post_id = extras.getString("id");
        }
        curr_post = new Post();

        db.collection("posts").document(post_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                curr_post = documentSnapshot.toObject(Post.class);
                curr_post.setPost_id(post_id);

                binding.titleTxt.setText(curr_post.getTitle());
                binding.categoryTxt.setText("Category: " + curr_post.getCategory());
                binding.publisherTxt.setText("Publisher Name: " + curr_post.getPublisher_email());
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


            }
        });
    }
}