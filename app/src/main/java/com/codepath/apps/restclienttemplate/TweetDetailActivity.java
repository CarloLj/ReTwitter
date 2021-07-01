package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {

    TextView tvBody;
    ImageView ivProfileImage;
    ImageView media;
    TextView tvScreenName;
    TextView tvUserName;
    TextView tvTimeStamp;
    ImageButton ibLike, ibRetweet, ibReply;
    TextView tvLike, tvRetweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);

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
        final Tweet tweet = Parcels.unwrap(intent.getParcelableExtra("tweet"));
        loadTweetObject(tweet);
    }

    private void loadTweetObject(Tweet tweet) {
        tvBody.setText(tweet.body);
        Glide.with(getBaseContext())
                .load(tweet.user.profileImageUrl)
                .transform(new CircleCrop())
                .into(ivProfileImage);
        Glide.with(getBaseContext())
                .load(tweet.mediaUrl)
                .transform(new RoundedCorners(20))
                .into(media);
        tvScreenName.setText(tweet.user.screenName);
        tvUserName.setText("@"+tweet.user.name);
        tvTimeStamp.setText(tweet.createdAt);
        tvLike.setText(String.valueOf(tweet.likeCount));
        tvRetweet.setText(String.valueOf(tweet.retweetCount));
    }
}