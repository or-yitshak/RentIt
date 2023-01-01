package com.example.rentmystuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notification_rec_view; // recycle view of the interested users.
    private NotificationRecViewAdapter adapter; //adapts between the recycler view and the design of the items inside.
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<Notification> notification_list; // A list representing the interested users.
    private FirebaseAuth auth = FirebaseAuth.getInstance();
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
                Intent intent = new Intent(NotificationActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            case R.id.logOutBtn:
                Intent intent2 = new Intent(NotificationActivity.this, LoginActivity.class);
                auth.signOut();
                startActivity(intent2);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifcation);

        getSupportActionBar().setTitle("NotificationActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing the recycler view and the interested list:
        notification_rec_view = findViewById(R.id.notificationRecView);
        notification_rec_view.setLayoutManager(new LinearLayoutManager(this));
        notification_list = new ArrayList<Notification>();

        //extracting from the firestore database the notification information:
        db.collection("users").document(auth.getCurrentUser().getEmail()).collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //If task is successful, insert interested user to the list:
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Notification notification = doc.toObject(Notification.class);
                        notification_list.add(notification);
                    }
                    adapter = new NotificationRecViewAdapter(NotificationActivity.this, notification_list);
                    notification_rec_view.setAdapter(adapter);
                    //If not, throw an error to the user:
                } else {
                    Toast.makeText(NotificationActivity.this, "An error has occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
