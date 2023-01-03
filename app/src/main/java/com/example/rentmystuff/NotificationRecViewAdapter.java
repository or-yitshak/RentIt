package com.example.rentmystuff;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentmystuff.databinding.ActivityProfileListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This is the ProfileRecViewAdapter class.
 * It adapts the profile list item XML to the recycler view.
 */

public class NotificationRecViewAdapter extends RecyclerView.Adapter<NotificationRecViewAdapter.ViewHolder> {
    private Context context; //reference to the activity that uses the adapter.
    private ArrayList<Notification> notification_list; // An ArrayList of notifications for user.
    private String post_id;
    private ActivityProfileListBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


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
        final String[] publisher_email = new String[1];
        db.collection("posts").document(curr_noti.getPost_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot doc) {
                Post post = doc.toObject(Post.class);
                post_id = post.getPost_id();
                publisher_email[0] = post.getPublisher_email();
                if(!auth.getCurrentUser().getEmail().equals(publisher_email[0])) {
                    //Using Picasso library to download an image using URL:
                    Picasso.get()
                        .load(post.getImageURL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(holder.profile_image);
                    db.collection("users").document(publisher_email[0]).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User curr_user = documentSnapshot.toObject(User.class);
                            String approved = "";
                            if (curr_noti.isApproved()) approved = "approved";
                            else approved = "declined";
                            holder.name_txt.setText(curr_user.getFirst_name() + " " + curr_user.getLast_name() + " has " + approved + " your request");
                            holder.email_txt.setText("Email: " + publisher_email[0]);
                        }
                    });
                }
                else{
                    db.collection("posts").document(post_id).collection("interested").document(curr_noti.getInterested_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Interested interested = documentSnapshot.toObject(Interested.class);
                            db.collection("users").document(interested.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User inter = documentSnapshot.toObject(User.class);
                                    Picasso.get()
                                            .load(inter.getImage_URL())
                                            .placeholder(R.mipmap.ic_launcher)
                                            .fit()
                                            .centerCrop()
                                            .into(holder.profile_image);

                                    holder.name_txt.setText(inter.getFirst_name() + " " + inter.getLast_name() + " wants to rent your " + post.getTitle());
                                    holder.email_txt.setText("Email: " + interested.getEmail());

                                }
                            });
                        }
                    });
                }
            }
        });

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
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name_txt;
        private TextView email_txt;
        private TextView date_txt;
        private ImageView profile_image;
        private CardView parent;
        private ImageButton check_btn;
        private ImageButton close_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.nameTxt);
            email_txt = itemView.findViewById(R.id.emailTxt);
            date_txt = itemView.findViewById(R.id.dateTxt);
            profile_image = itemView.findViewById(R.id.profileImageView);
            parent = itemView.findViewById(R.id.parent);
            check_btn = itemView.findViewById(R.id.checkBtn);
            close_btn = itemView.findViewById(R.id.closeBtn);
        }
    }
}
