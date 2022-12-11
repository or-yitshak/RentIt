package com.example.rentmystuff;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileRecViewAdapter extends RecyclerView.Adapter<ProfileRecViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Interested> users = new ArrayList<>();
    private String parent;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileRecViewAdapter(Context context, ArrayList<Interested> users, String parent) {
        this.context = context;
        this.users = users;
        this.parent = parent;
    }

    @NonNull
    @Override
    public ProfileRecViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.profile_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileRecViewAdapter.ViewHolder holder, int position) {
        Interested curr_inter = this.users.get(position);
        db.collection("users").document(curr_inter.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User curr_user = documentSnapshot.toObject(User.class);
                holder.name_txt.setText(curr_user.getFirst_name() + " " + curr_user.getLast_name());
                Picasso.get()
                        .load(curr_user.getImage_URL())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(holder.profile_image);
            }
        });
        holder.email_txt.setText(curr_inter.getEmail());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, curr_inter.getTitle() + " Selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                intent.putExtra("email", curr_inter.getEmail());
                view.getContext().startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        /*
            this class responsible of holding the view items for every item in our
            recycler view
         */
        private TextView name_txt;
        private TextView email_txt;
        private ImageView profile_image;
        private CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.nameTxt);
            email_txt = itemView.findViewById(R.id.emailTxt);
            profile_image = itemView.findViewById(R.id.profileImageView);
            parent = itemView.findViewById(R.id.parent);

        }
    }
}
