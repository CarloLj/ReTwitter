package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import okhttp3.Headers;

import static androidx.core.content.ContextCompat.startActivity;

public class TweetDetailActivity extends AppCompatActivity {

    TextView tvBody;
    ImageView ivProfileImage;
    ImageView media;
    TextView tvScreenName;
    TextView tvUserName;
    TextView tvTimeStamp;
    ImageButton ibLike, ibRetweet, ibReply;
    TextView tvLike, tvRetweet;
    Tweet tweet;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

        client = TwitterApp.getRestClient(this);

        tvBody = findViewById(R.id.tvBody);
        ivProfileImage = findViewById(R.id.ivProfileimage);
        media = findViewById(R.id.ivTweetImage);
        tvScreenName = findViewById(R.id.tvScreenName);
        tvUserName = findViewById(R.id.tvUserName);
        tvTimeStamp = findViewById(R.id.tvCreatedAt);
        ibLike = findViewById(R.id.ibLike);
        ibReply = findViewById(R.id.ibReply);
        ibRetweet = findViewById(R.id.ibRetweet);
        tvLike = findViewById(R.id.tvLike);
        tvRetweet = findViewById(R.id.tvRetweet);

        Intent intent = getIntent();
        tweet = Parcels.unwrap(intent.getParcelableExtra("tweet"));
        loadTweetObject(tweet);
    }

    private void loadTweetObject(Tweet tweet) {
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
        tvBody.setText(tweet.body);
        Glide.with(getBaseContext())
                .load(tweet.user.profileImageUrl)
                .transform(new CircleCrop())
                .into(ivProfileImage);
        Glide.with(getBaseContext())
                .load(tweet.mediaUrl)
                .transform(new RoundedCorners(20))
                .into(media);
        if(tweet.mediaUrl==""){
            media.setVisibility(View.INVISIBLE);
        }
        tvScreenName.setText(tweet.user.screenName);
        tvUserName.setText("@"+tweet.user.name);
        tvTimeStamp.setText(tweet.createdAt);
        tvLike.setText(String.valueOf(tweet.likeCount));
        tvRetweet.setText(String.valueOf(tweet.retweetCount));
    }


    public void replyFunction(View view) {
        Toast.makeText(TweetDetailActivity.this, "Reply Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(TweetDetailActivity.this, com.codepath.apps.restclienttemplate.ReplyActivity.class);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        startActivity(intent);
    }

    public void retweetFunction(View view) {
        if(!tweet.retweeted) {
            client.retweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    ibRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                    int retweets = Integer.valueOf(tvRetweet.getText().toString());
                    tvRetweet.setText(String.valueOf(retweets+1));
                    tweet.retweetCount++;
                    tweet.retweeted = true;
                    Toast.makeText(TweetDetailActivity.this, "Retweeted successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailActivity.this, "Could not retweet!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(TweetDetailActivity.this, "Uneretweeted successfully!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailActivity.this, "Could not retweet!", Toast.LENGTH_SHORT).show();
                    Log.i("Unretweet tweet", "failure" + response);
                }
            });
        }
    }

    public void likeButton(View view) {
        if(!tweet.liked) {
            client.like(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    ibLike.setBackgroundResource(R.drawable.ic_vector_heart);
                    int nLikes = Integer.valueOf(tvLike.getText().toString());
                    tvLike.setText(String.valueOf(nLikes+1));
                    tweet.liked = true;
                    tweet.likeCount = tweet.likeCount+1;
                    Toast.makeText(TweetDetailActivity.this, "Tweet Liked!", Toast.LENGTH_SHORT).show();
                    Log.i("OnClick Like", "successful");
                }
                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailActivity.this, "Error liking the tweet, try again later!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(TweetDetailActivity.this, "Tweet Disliked!", Toast.LENGTH_SHORT).show();
                    Log.i("OnClick Like", "success");
                }

                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Toast.makeText(TweetDetailActivity.this, "Tweet could not be Disliked!", Toast.LENGTH_SHORT).show();
                    Log.e("OnClick Like", "error");
                }
            });
        }
    }
}