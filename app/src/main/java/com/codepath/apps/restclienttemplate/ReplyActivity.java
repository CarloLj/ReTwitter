package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import okhttp3.Headers;

public class ReplyActivity extends AppCompatActivity {

    public static final String TAG = "ReplyActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    //Button to answer
    Button btnTweet;
    //Object received
    Tweet receivedTweet;

    //Data from the user
    TextView tvScreenName;
    ImageView ivProfileimage;
    TextView tvUserName;
    TextView tvTweetCreator;
    TextView tvCreatedAt;
    TextView tvBody;

    //Data to send
    EditText etCompose;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        client = TwitterApp.getRestClient(this);

        receivedTweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        btnTweet = findViewById(R.id.btnTweet);

        tvScreenName = findViewById(R.id.tvScreenName);
        ivProfileimage = findViewById(R.id.ivProfileimage);
        tvUserName = findViewById(R.id.tvUserName);
        tvTweetCreator = findViewById(R.id.tvTweetCreator);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvBody = findViewById(R.id.tvBody);

        etCompose = findViewById(R.id.etCompose);

        tvTweetCreator.setText(receivedTweet.user.screenName);
        tvScreenName.setText(receivedTweet.user.screenName);
        Glide.with(ReplyActivity.this).
                load(receivedTweet.mediaUrl).
                transform(new CircleCrop()).
                into(ivProfileimage);
        tvUserName.setText(receivedTweet.user.name);
        String string = "@"+ receivedTweet.user.name;
        tvBody.setText(receivedTweet.body);
        tvCreatedAt.setText(receivedTweet.createdAt);
        etCompose.setText(string);

        //Set Click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ReplyActivity.this, "Sorry your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ReplyActivity.this, "Sorry your tweet is too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Make an API call to Twitter to publish the Tweet
                client.replyTweet(receivedTweet.id, tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "OnSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Response updated. Check Twitter");
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(ReplyActivity.this, "something failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "OnFailure to reply to tweet", throwable);
                    }
                });
            }
        });
    }
}
