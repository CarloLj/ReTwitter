package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;
    TwitterClient client;

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

    public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client) {
        this.context = context;
        this.tweets = tweets;
        this.client = client;
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

        public void bind(final Tweet tweet) {
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

            if (tweet.liked) {
                ibLike.setBackgroundResource(R.drawable.ic_vector_heart);
            }else{
                ibLike.setBackgroundResource(R.drawable.ic_like_anim);
            }

            if (tweet.retweeted) {
                ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
            }else{
                ibRetweet.setBackgroundResource(R.drawable.ic_retweet_anim);
            }

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!tweet.liked) {
                        client.like(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ibLike.setBackgroundResource(R.drawable.ic_vector_heart);
                                int nLikes = Integer.valueOf(tvLike.getText().toString());
                                tvLike.setText(String.valueOf(nLikes+1));
                                tweet.liked = true;
                                tweet.likeCount = tweet.likeCount+1;
                                Toast.makeText(context, "Tweet Liked!", Toast.LENGTH_SHORT).show();
                                Log.i("OnClick Like", "successful");
                            }
                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context, "Error liking the tweet, try again later!", Toast.LENGTH_SHORT).show();
                                Log.e("OnClick Like", "error");
                            }
                        });
                    }
                    else {
                        client.disLike(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ibLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                                int favorites = Integer.valueOf(tvLike.getText().toString()) ;
                                tvLike.setText(String.valueOf(favorites-1));
                                tweet.likeCount = tweet.likeCount-1;
                                tweet.liked = false;
                                Toast.makeText(context, "Tweet Disliked!", Toast.LENGTH_SHORT).show();
                                Log.i("OnClick Like", "success");
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context, "Tweet could not be Disliked!", Toast.LENGTH_SHORT).show();
                                Log.e("OnClick Like", "error");
                            }
                        });
                    }
                }
            });

            ibRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!tweet.retweeted) {
                        client.retweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                                int retweets = Integer.valueOf(tvRetweet.getText().toString());
                                tvRetweet.setText(String.valueOf(retweets+1));
                                tweet.retweetCount++;
                                tweet.retweeted = true;
                                Toast.makeText(context, "Retweeted successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context, "Could not retweet!", Toast.LENGTH_SHORT).show();
                                Log.i("Retweet tweet", "failure" + response);
                            }
                        });
                    }else{
                        client.unRetweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                                int retweets = Integer.valueOf(tvRetweet.getText().toString());
                                tvRetweet.setText(String.valueOf(retweets-1));
                                tweet.retweetCount--;
                                tweet.retweeted = false;
                                Toast.makeText(context, "Uneretweeted successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Toast.makeText(context, "Could not retweet!", Toast.LENGTH_SHORT).show();
                                Log.i("Unretweet tweet", "failure" + response);
                            }
                        });
                    }
                }
            });

            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Reply Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION)
            {
                Intent intent = new Intent(context, TweetDetailActivity.class);
                Tweet tweet = tweets.get(position);
                intent.putExtra("tweet", Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }
}
