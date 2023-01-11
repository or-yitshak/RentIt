package com.example.rentmystuff.notificationViewList;

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

import com.example.rentmystuff.HomeActivity;
import com.example.rentmystuff.R;
import com.example.rentmystuff.classes.Notification;
import com.example.rentmystuff.interfaces.NotificationFirestoreCallback;
import com.example.rentmystuff.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class NotificationActivity extends AppCompatActivity implements Observer {

    private RecyclerView notification_rec_view; // recycle view of the interested users.
    private NotificationRecViewAdapter adapter; //adapts between the recycler view and the design of the items inside.
    private ArrayList<Notification> notification_list; // A list representing the interested users.
    private NotificationModel notification_model;
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
                notification_model.signOut();
                startActivity(intent2);
                return true;
            case R.id.notificationBtn:
                Intent intent3 = new Intent(NotificationActivity.this, NotificationActivity.class);
                startActivity(intent3);
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

        notification_model = new NotificationModel();
        notification_model.addObserver(this);

        adapter = new NotificationRecViewAdapter(NotificationActivity.this, notification_list);
        notification_rec_view.setAdapter(adapter);

        notification_model.getNotifications(notification_list, new NotificationFirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Notification> list) {
                adapter.setNotification_list(list);
            }
        });


    }

    @Override
    public void update(Observable observable, Object o) {
        if(o instanceof String){
            Toast.makeText(NotificationActivity.this, o.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
