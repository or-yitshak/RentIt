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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * This is the ProfileRecViewAdapter class.
 * It adapts the profile list item XML to the recycler view.
 */

public class ProfileRecViewAdapter extends RecyclerView.Adapter<ProfileRecViewAdapter.ViewHolder> {
    private Context context; //reference to the activity that uses the adapter.
    private ArrayList<Interested> interested_list; // An ArrayList of interested users.
    private String parent; //string representing from which class we arrived to profileListView.
    private ActivityProfileListBinding binding;
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProfileListModel profile_list_model;

    /**
     * Constructor for the ProfileRecViewAdapter class.
     */
    public ProfileRecViewAdapter(Context context, ArrayList<Interested> users, String parent, ProfileListModel profile_list_model) {
        this.context = context;
        this.interested_list = users;
        this.profile_list_model = profile_list_model;
        this.parent = parent;
    }

    public void setInterested_list(ArrayList<Interested> interested_list) {
        this.interested_list = interested_list;
        notifyDataSetChanged();
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
        holder.view_holder_model.setOnBindViewHolder(curr_inter);

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

        holder.check_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.view_holder_model.updateApproved(curr_inter);

                holder.check_btn.setVisibility(view.GONE);
                holder.close_btn.setVisibility(view.GONE);

                profile_list_model.addNotification(curr_inter, true);
            }
        });

        holder.close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.check_btn.setVisibility(view.GONE);
                holder.close_btn.setVisibility(view.GONE);
                interested_list.remove(curr_inter);
                notifyDataSetChanged();
                profile_list_model.deleteInterested(curr_inter,context);

                holder.check_btn.setVisibility(view.GONE);
                holder.close_btn.setVisibility(view.GONE);
                profile_list_model.addNotification(curr_inter, false);
            }
        });
    }

    /**
     * This function returns the size of the interested users.
     */
    @Override
    public int getItemCount() {
        return this.interested_list.size();
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
            if(o instanceof User){
                User curr_user = (User) o;
                        this.name_txt.setText("Full Name: " + curr_user.getFirst_name() + " " + curr_user.getLast_name());
                //Using Picasso library to download an image using URL:
                Picasso.get()
                        .load(curr_user.getImage_URL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(this.profile_image);
            }
            if(o instanceof String){
                String str = (String) o;

            }


        }
    }
}
