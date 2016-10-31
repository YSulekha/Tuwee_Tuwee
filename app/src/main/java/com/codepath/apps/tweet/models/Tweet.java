package com.codepath.apps.tweet.models;

//Model class for Tweets

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tweet {

    private long tweetId;
    //Created At format - Sun Oct 30 03:00:01 +0000 2016
    private String createdAt;

    public long getTweetId() {
        return tweetId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    private User user;
    private String body;

    public Tweet(JSONObject jsonObject){
        try {
            this.body = jsonObject.getString("text");
            this.createdAt = jsonObject.getString("created_at");
            this.user = new User(jsonObject.getJSONObject("user"));
            this.tweetId = jsonObject.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray result){
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i=0;i < result.length();i++){
            JSONObject tweetObject = null;
            try {
                tweetObject = result.getJSONObject(i);
            } catch (JSONException e) {
                continue;
            }
            tweets.add(new Tweet(tweetObject));
        }
        return tweets;
    }

}

