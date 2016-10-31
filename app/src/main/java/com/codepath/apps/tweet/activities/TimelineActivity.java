package com.codepath.apps.tweet.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.tweet.R;
import com.codepath.apps.tweet.TwitterApplication;
import com.codepath.apps.tweet.TwitterClient;
import com.codepath.apps.tweet.adapters.TweetsArrayAdapter;
import com.codepath.apps.tweet.databinding.ActivityTimelineBinding;
import com.codepath.apps.tweet.fragments.ComposeDialog;
import com.codepath.apps.tweet.models.Tweet;
import com.codepath.apps.tweet.utils.EndlessScrollViewListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class TimelineActivity extends AppCompatActivity implements ComposeDialog.SaveFilterListener {

    private TwitterClient twitterClient;
    private TweetsArrayAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Tweet> tweets;
    private EndlessScrollViewListener endlessScrollViewListener;
    //Applying Data Binding for Views
    ActivityTimelineBinding timelineBinding;
    private long sinceId = 0;
    private long maxId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timelineBinding = DataBindingUtil.setContentView(this,R.layout.activity_timeline);
       // setContentView(R.layout.activity_timeline);
        Toolbar toolbar = timelineBinding.toolbar;
        setSupportActionBar(toolbar);

        //Configure recycler view
        recyclerView = timelineBinding.contentTimeline.timelineRecycler;
        tweets = new ArrayList<Tweet>();
        recyclerAdapter = new TweetsArrayAdapter(this,tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        endlessScrollViewListener = new EndlessScrollViewListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.v("lastTweet+page", String.valueOf(page));
                populateTimeline(false,false);
            }
        };
        recyclerView.addOnScrollListener(endlessScrollViewListener);


        FloatingActionButton fab = timelineBinding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                FragmentManager fm =getSupportFragmentManager();
                ComposeDialog dialog = ComposeDialog.newInstance(null);
                dialog.show(fm,"Dialog");
            }
        });
        swipeRefreshLayout = timelineBinding.contentTimeline.swipeContainer;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(true,true);
            }
        });
        //Singleton client
        twitterClient = TwitterApplication.getRestClient();
        //populate timeline
        populateTimeline(true,false);
    }

    //Method to fetch the tweets to populate timeline
    private void populateTimeline(boolean isFirstTime, final boolean isRefresh) {
        Log.v("lastTweet", String.valueOf(maxId)+" "+tweets.size());
        RequestParams params = new RequestParams();
        params.put("count",40);
        if(!isFirstTime){
            params.put("max_id",maxId);
        }
        if(isRefresh){
            params.put("since_id",sinceId);
        }
        else{
            params.put("since_id",1);
        }
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.v("response",response.toString());
                if(isRefresh){
                    Log.v("isRefresh", String.valueOf(tweets.size()));
                    tweets.addAll(0,Tweet.fromJSONArray(response));
                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    tweets.addAll(Tweet.fromJSONArray(response));

                }
                maxId = tweets.get(tweets.size() - 1).getTweetId() - 1;
                Log.v("max+since", String.valueOf(maxId));
                Log.v("lastTweet", tweets.get(tweets.size() - 1).getBody());
                sinceId = tweets.get(0).getTweetId();
                Log.v("max+since1", String.valueOf(sinceId));
                recyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("failure",errorResponse.toString());
            }


        },params);
    }

//Callback method from Dialog fragment
    @Override
    public void onTweet(String status) {
        Toast.makeText(this,status,Toast.LENGTH_SHORT).show();
        RequestParams params = new RequestParams();
        params.put("status",status);
        twitterClient.tweetStatus(new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.v("OnSuccess","sad");
              //  tweets.clear();
                //endlessScrollViewListener.resetState();
                populateTimeline(true,true);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("failure",errorResponse.toString());
            }
        },params);
    }
}
