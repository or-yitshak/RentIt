package com.example.rentmystuff.notificationViewList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentmystuff.R;
import com.example.rentmystuff.ViewHolderModel;
import com.example.rentmystuff.classes.Interested;
import com.example.rentmystuff.classes.Notification;
import com.example.rentmystuff.classes.User;
import com.example.rentmystuff.databinding.ActivityProfileListBinding;
import com.example.rentmystuff.classes.Post;
import com.example.rentmystuff.post.PostPageActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * This is the ProfileRecViewAdapter class.
 * It adapts the profile list item XML to the recycler view.
 */

public class NotificationRecViewAdapter extends RecyclerView.Adapter<NotificationRecViewAdapter.ViewHolder> {
    private Context context; //reference to the activity that uses the adapter.
    private ArrayList<Notification> notification_list; // An ArrayList of notifications for user.
//    private String post_id;
//    private ActivityProfileListBinding binding;
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void setNotification_list(ArrayList<Notification> notification_list) {
        this.notification_list = notification_list;
        notifyDataSetChanged();
    }

    /**
     * Constructor for the ProfileRecViewAdapter class.
     */
    public NotificationRecViewAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        Collections.sort(notifications, Collections.reverseOrder());//displaying latest notifications first
        this.notification_list = notifications;
    }

    /**
     * This function creates a view holder that contains all thr views in profile_list_item XML.
     * The ViewHolder job is to contain the many references of the page. This gives us access
     * to those references.
     */
    @NonNull
    @Override
    public NotificationRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.profile_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification curr_noti = this.notification_list.get(position);
        holder.view_holder_model.setNotificationOnBindViewHolder(curr_noti);

        holder.date_txt.setText("Date: " + curr_noti.getDate());

        //Sending the user to the correct profile page when clicking on the interested user:
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostPageActivity.class);
                intent.putExtra("id", curr_noti.getPost_id());
                view.getContext().startActivity(intent);
            }
        });
        holder.check_btn.setVisibility(View.GONE);
        holder.close_btn.setVisibility(View.GONE);

    }


    /**
     * This function returns the size of the interested users.
     */
    @Override
    public int getItemCount() {
        return notification_list.size();
    }


    /**
     * This class is responsible for holding the view items of every item in our recycler view.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements Observer {
        private TextView name_txt;
        private TextView email_txt;
        private TextView date_txt;
        private ImageView profile_image;
        private CardView parent;
        private ImageButton check_btn;
        private ImageButton close_btn;
        private ViewHolderModel view_holder_model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.nameTxt);
            email_txt = itemView.findViewById(R.id.emailTxt);
            date_txt = itemView.findViewById(R.id.dateTxt);
            profile_image = itemView.findViewById(R.id.profileImageView);
            parent = itemView.findViewById(R.id.parent);
            check_btn = itemView.findViewById(R.id.checkBtn);
            close_btn = itemView.findViewById(R.id.closeBtn);

            view_holder_model = new ViewHolderModel();
            view_holder_model.addObserver(this);
        }

        @Override
        public void update(Observable observable, Object o) {
            if(o instanceof Post){
                Post post = (Post) o;
                Picasso.get()
                        .load(post.getImageURL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(this.profile_image);
            }
            if(o instanceof String[]){
                String[] ans = (String[]) o;
                this.name_txt.setText(ans[0]);
                this.email_txt.setText(ans[1]);
            }
            if(o instanceof User){
                User inter = (User) o;
                Picasso.get()
                        .load(inter.getImage_URL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(this.profile_image);
            }

        }
    }
}
