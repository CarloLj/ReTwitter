package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    //Pass in the context and list of tweets1||1||||1|
    @NonNull
    @NotNull
    // For each row, inflate the layout
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return  new ViewHolder(view);
    }

    //Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        //Get data at position
        Tweet tweet = tweets.get(position);
        //Bind the tweet with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvUserName;
        TextView tvCreatedAt;
        ImageView ivTweetImage;
        ImageButton ibReply, ibRetweet, ibLike;
        TextView tvRetweet, tvLike;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileimage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivTweetImage = itemView.findViewById(R.id.ivTweetImage);
            tvUserName = itemView.findViewById(R.id.tvUserName);

            //Tweet buttons (Like, Reply, Retweet)
            ibReply = itemView.findViewById(R.id.ibReply);
            ibRetweet = itemView.findViewById(R.id.ibRetweet);
            ibLike = itemView.findViewById(R.id.ibLike);

            //Tweet stats (Like, Reply, Retweet)
            tvRetweet = itemView.findViewById(R.id.tvRetweet);
            tvLike = itemView.findViewById(R.id.tvLike);

            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvCreatedAt.setText("• " + tweet.createdAt);
            tvUserName.setText(tweet.user.name);

            tvLike.setText(String.valueOf(tweet.likeCount));
            tvRetweet.setText(String.valueOf(tweet.retweetCount));

            Glide.with(context).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
            if(tweet.mediaUrl != null){
                ivTweetImage.setVisibility(View.VISIBLE);
            }else{
                ivTweetImage.setVisibility(View.GONE);
            }
            Glide.with(context).load(tweet.mediaUrl).transforms(new CenterCrop(), new RoundedCorners(30)).into(ivTweetImage);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Clicked on the tweet!", Toast.LENGTH_SHORT).show();
        }
    }
}
