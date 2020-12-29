package com.example.clone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clone.Model.Post;
import com.example.clone.Model.User;
import com.example.clone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.os.Build.VERSION_CODES.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewholder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View view = LayoutInflater.from(mContext).inflate(com.example.clone.R.layout.post_item,parent,false);
        return new PostAdapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post= mPost.get(position);
        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);

        if (post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());

        }
        publisherInfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());
        isLikes(post.getPostid(),holder.like);
        nrLikes(holder.likes, post.getPostid());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                }else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public  class viewholder extends RecyclerView.ViewHolder{

      public ImageView image_profile ,post_image,like,comment,save;
      public TextView username , likes , publisher , description ,comments;


        public viewholder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(com.example.clone.R.id.image_profile);
            post_image = itemView.findViewById(com.example.clone.R.id.post_image);
            like = itemView.findViewById(com.example.clone.R.id.post_like);
            comment = itemView.findViewById(com.example.clone.R.id.comment);
            save = itemView.findViewById(com.example.clone.R.id.post_save);
            username = itemView.findViewById(com.example.clone.R.id.user_name);
            likes = itemView.findViewById(com.example.clone.R.id.likes);
            publisher = itemView.findViewById(com.example.clone.R.id.publisher);
            description = itemView.findViewById(com.example.clone.R.id.description);
            comments = itemView.findViewById(com.example.clone.R.id.post_comment);

        }
    }

    private void isLikes(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(com.example.clone.R.drawable.ic_liked);
                    imageView.setTag("liked");
                }else {
                    imageView.setImageResource(com.example.clone.R.drawable.ic_post_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void nrLikes(final TextView likes , String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                likes.setText(snapshot.getChildrenCount()+"likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void  publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
