package com.codepath.apps.tweet.models;

//Model class for User

import com.codepath.apps.tweet.database.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Table(database = TwitterDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel {

    @Column
    private String userName;
    @Column
    private String screenName;
    @PrimaryKey
    @Column
    private long userId;
    @Column
    private String imageUrl;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



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




   /* public User(JSONObject jsonObject){
        try {
            this.userName = jsonObject.getString("name");
            this.screenName = "@"+jsonObject.getString("screen_name");
            this.imageUrl = jsonObject.getString("profile_image_url");
            this.userId = jsonObject.getLong("id");


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }*/

    public static User fromJSONObject(JSONObject jsonObject){
        User user = new User();
        try {
            user.userName = jsonObject.getString("name");
            user.screenName = "@"+jsonObject.getString("screen_name");
            user.imageUrl = jsonObject.getString("profile_image_url");
            user.userId = jsonObject.getLong("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
