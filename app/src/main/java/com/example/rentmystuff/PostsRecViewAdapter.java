package com.example.rentmystuff;

import static androidx.core.content.ContextCompat.startActivity;
import androidx.appcompat.app.AppCompatActivity;


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

    private Context context;
    private ArrayList<Post> posts = new ArrayList<>();

    public PostsRecViewAdapter(Context context, ArrayList<Post> posts) {
        this.context=context;
        this.posts=posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(this.context).inflate(R.layout.posts_list_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post current_post = this.posts.get(position);
        holder.post_title_txt.setText(current_post.getTitle());
        holder.post_name_txt.setText(current_post.getPublisher_email());
        Picasso.get()
                .load(current_post.getImageURL())
                .fit()
                .centerCrop()
                .into(holder.post_image);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, current_post.getTitle() + " Selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(), PostPageActivity.class);
                intent.putExtra("id",current_post.getPost_id());
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();//this way we will refresh the recycler view with the new data we received
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        /*
            this class responsible of holding the view items for every item in our
            recycler view
         */
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
