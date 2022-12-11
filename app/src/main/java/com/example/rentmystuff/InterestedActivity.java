package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InterestedActivity extends AppCompatActivity {
    private String post_id;
    private RecyclerView interested_rec_view;
    private ProfileRecViewAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Interested> interested_list;
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
                Intent intent = new Intent(InterestedActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(InterestedActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);


//        getSupportActionBar().setTitle("PostsListActivity");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            post_id = extras.getString("id");
        }

        interested_rec_view = findViewById(R.id.interestedRecView);
        interested_rec_view.setLayoutManager(new GridLayoutManager(this, 2));
        interested_list = new ArrayList<Interested>();

        db.collection("posts").document(post_id).collection("interested").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        Interested curr_inter = doc.toObject(Interested.class);
                        interested_list.add(curr_inter);
                    }
                    adapter = new ProfileRecViewAdapter(InterestedActivity.this, interested_list, "MyProfileActivity");
                    interested_rec_view.setAdapter(adapter);
                }else{
                    Toast.makeText(InterestedActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
