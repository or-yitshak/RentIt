package com.example.rentmystuff;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentmystuff.databinding.ActivityPostPageBinding;
import com.example.rentmystuff.databinding.ActivityProfileListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * This is the ProfileRecViewAdapter class.
 * It adapts the profile list item XML to the recycler view.
 */

public class ProfileRecViewAdapter extends RecyclerView.Adapter<ProfileRecViewAdapter.ViewHolder> {
    private Context context; //reference to the activity that uses the adapter.
    private ArrayList<Interested> interested_list; // An ArrayList of interested users.
    private String parent; //string representing from which class we arrived to profileListView.
    private ActivityProfileListBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Constructor for the ProfileRecViewAdapter class.
     */
    public ProfileRecViewAdapter(Context context, ArrayList<Interested> users, String parent) {
        this.context = context;
        this.interested_list = users;
        this.parent = parent;
    }

    /**
     * This function creates a view holder that contains all thr views in profile_list_item XML.
     * The ViewHolder job is to contain the many references of the page. This gives us access
     * to those references.
     */
    @NonNull
    @Override
    public ProfileRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.profile_list_item, parent, false);
        return new ViewHolder(v);
    }

    /**
     * This function creates the card view for each profile in the list and initializes all its variables.
     */
    @Override
    public void onBindViewHolder(@NonNull ProfileRecViewAdapter.ViewHolder holder, int position) {
        Interested curr_inter = this.interested_list.get(position);
        db.collection("users").document(curr_inter.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User curr_user = documentSnapshot.toObject(User.class);
                holder.name_txt.setText("Full Name: " + curr_user.getFirst_name() + " " + curr_user.getLast_name());
                //Using Picasso library to download an image using URL:
                Picasso.get()
                        .load(curr_user.getImage_URL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(holder.profile_image);
            }
        });
        holder.email_txt.setText("Email: " + curr_inter.getEmail());
        holder.date_txt.setText("Date: " + curr_inter.getDate());

        //Sending the user to the correct profile page when clicking on the interested user:
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                intent.putExtra("email", curr_inter.getEmail());
                view.getContext().startActivity(intent);
            }
        });

        if(curr_inter.isApproved()){
            holder.check_btn.setVisibility(View.GONE);
            holder.close_btn.setVisibility(View.GONE);
        }
        final String[] title = new String[2];
        db.collection("posts").document(curr_inter.getPost_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Post pst = documentSnapshot.toObject(Post.class);
                title[0] = pst.getTitle();
//                        title[1] = pst.getPublisher_email();
            }
        });

        holder.check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("posts").document(curr_inter.getPost_id())
                        .collection("interested")
                        .document(curr_inter.getInterested_id())
                        .update("approved",true);
                holder.check_btn.setVisibility(view.GONE);
                holder.close_btn.setVisibility(view.GONE);

                addNotification(curr_inter, true);
            }
        });

        holder.close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.check_btn.setVisibility(view.GONE);
                holder.close_btn.setVisibility(view.GONE);
                interested_list.remove(curr_inter);
                notifyDataSetChanged();
                db.collection("posts")
                        .document(curr_inter.getPost_id())
                        .collection("interested")
                        .document(curr_inter.getInterested_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "the request has been removed!", Toast.LENGTH_SHORT).show();

                            }
                        });
                addNotification(curr_inter, false);
            }
        });
    }

    private void addNotification(Interested curr_inter, boolean flag){
        Notification notification = new Notification(curr_inter.getPost_id(), flag);
        notification.setDate(curr_inter.getDate());
        db.collection("users")
                .document(curr_inter.getEmail())
                .collection("notifications").add(notification).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        db.collection("users")
                                .document(curr_inter.getEmail())
                                .collection("notifications")
                                .document(documentReference.getId())
                                .update("notification_id",documentReference.getId());
                    }
                });
    }

    /**
     * This function returns the size of the interested users.
     */
    @Override
    public int getItemCount() {
        return interested_list.size();
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
