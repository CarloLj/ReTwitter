package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

//Need to declare this line in order to declare the object parcelable
@Parcel
public class User {

    public String name;
    public String screenName;
    public String profileImageUrl;

    //Need empty constructor for parcelable object
    public User(){ }

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }
}
