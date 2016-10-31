package com.codepath.apps.tweet.models;

//Model class for User

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String userName;
    private String screenName;
    private long userId;

    public String getUserName() {
        return userName;
    }

    public String getScreenName() {
        return screenName;
    }

    public long getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private String imageUrl;


    public User(JSONObject jsonObject){
        try {
            this.userName = jsonObject.getString("name");
            this.screenName = "@"+jsonObject.getString("screen_name");
            this.imageUrl = jsonObject.getString("profile_image_url");
            this.userId = jsonObject.getLong("id");


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
