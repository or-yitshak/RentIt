package com.example.rentmystuff;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PostsRecViewAdapter extends RecyclerView.Adapter<PostsRecViewAdapter.ViewHolder> {
    /**
     * This is the PostsRecViewAdapter class, it extends the RecyclerView adapter.
     * It adapts the post list item XML to the recycler view.
     */

    private Context context; //reference to the activity that uses the adapter.
    private ArrayList<Post> posts; // array of posts.
    private String parent; //string representing from which class we arrived to postListView.

    /**
     * Constructor for the PostsRecViewAdapter class.
     */
    public PostsRecViewAdapter(Context context, ArrayList<Post> posts, String parent) {
        this.context = context;
        this.posts = posts;
        this.parent = parent;
    }


    /**
     * This function creates a view holder that contains all thr views in post_list_item XML.
     * The ViewHolder job is to contain the many references of the page. This gives us access
     * to those references.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.posts_list_item, parent, false);
        return new ViewHolder(v);
    }

    /**
     * This function creates the card view for each post in the list and initializes all its variables.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post current_post = this.posts.get(position);
        holder.post_title_txt.setText(current_post.getTitle());
        holder.post_name_txt.setText(current_post.getPublisher_email());
        //Using Picasso library to download an image using URL:
        Picasso.get()
                .load(current_post.getImageURL())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(holder.post_image);

        //Sending the user to the correct post page when clicking on the post:
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parent.equals("PostsListActivity")) {
                    Toast.makeText(context, current_post.getTitle() + " Selected!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), PostPageActivity.class);
                    intent.putExtra("id", current_post.getPost_id());
                    view.getContext().startActivity(intent);
                } else if (parent.equals("MyProfileActivity")) {

                }
            }
        });

    }

    /**
     * This function returns the post list size.
     */
    @Override
    public int getItemCount() {
        return posts.size();
    }


//    public void setPosts(ArrayList<Post> posts) {
//        this.posts = posts;
//        notifyDataSetChanged();//this way we will refresh the recycler view with the new data we received
//    }

    /**
     * This class is responsible for holding the view items of every item in our recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView post_title_txt;
        private TextView post_name_txt;
        private ImageView post_image;
        private CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_title_txt = itemView.findViewById(R.id.postTitleTxt);
            post_name_txt = itemView.findViewById(R.id.postNameTxt);
            post_image = itemView.findViewById(R.id.postImageView);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}