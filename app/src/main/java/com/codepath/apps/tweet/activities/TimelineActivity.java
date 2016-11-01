package com.codepath.apps.tweet.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import com.codepath.apps.tweet.models.Tweet_Table;
import com.codepath.apps.tweet.utils.DividerItemDecoration;
import com.codepath.apps.tweet.utils.EndlessScrollViewListener;
import com.codepath.apps.tweet.utils.ItemClickSupport;
import com.codepath.apps.tweet.utils.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.raizlabs.android.dbflow.sql.language.SQLite.select;


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
    public static final int DETAIL_TWEET = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        timelineBinding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        // setContentView(R.layout.activity_timeline);
        Toolbar toolbar = timelineBinding.toolbar;
        setSupportActionBar(toolbar);

        //Configure recycler view
        recyclerView = timelineBinding.contentTimeline.timelineRecycler;
        tweets = new ArrayList<Tweet>();
        recyclerAdapter = new TweetsArrayAdapter(this, tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        endlessScrollViewListener = new EndlessScrollViewListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimeline(false, false);
            }
        };
        recyclerView.addOnScrollListener(endlessScrollViewListener);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        recyclerView.addItemDecoration(itemDecoration);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(v.getContext(), TwitterDetailActivity.class);
                Tweet t = tweets.get(position);
                intent.putExtra("tweet", Parcels.wrap(t));
                startActivityForResult(intent, DETAIL_TWEET);

            }
        });


        FloatingActionButton fab = timelineBinding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utility.isNetworkAvailable(view.getContext())) {
                    //  Organization_Table.name.is("CodePath")
                    tweets.addAll(select().from(Tweet.class).orderBy(Tweet_Table.createdAt,false).queryList());
                    recyclerAdapter.notifyDataSetChanged();
                    Toast.makeText(view.getContext(),getResources().getString(R.string.no_network),Toast.LENGTH_LONG).show();
                }
                else {
                    FragmentManager fm = getSupportFragmentManager();
                    ComposeDialog dialog = ComposeDialog.newInstance(null);
                    dialog.show(fm, "Dialog");
                }
            }
        });
        swipeRefreshLayout = timelineBinding.contentTimeline.swipeContainer;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeline(true, true);
            }
        });
        //Singleton client
        twitterClient = TwitterApplication.getRestClient();
        if (!Utility.isNetworkAvailable(this)) {
          //  Organization_Table.name.is("CodePath")
            tweets.addAll(select().from(Tweet.class).orderBy(Tweet_Table.createdAt,false).queryList());
            recyclerAdapter.notifyDataSetChanged();
            Toast.makeText(this,getResources().getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        } else {
            //populate timeline
            populateTimeline(true, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("OnActivityResult","dds");
        // Check which request we're responding to
        if (requestCode == DETAIL_TWEET) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
               if(data.getBooleanExtra("is_reply",false)){
                 RequestParams params = data.getParcelableExtra("params");
                   onTweet(params);
               }
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //Method to fetch the tweets to populate timeline
    private void populateTimeline(boolean isFirstTime, final boolean isRefresh) {
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (!isFirstTime) {
            params.put("max_id", maxId);
        }
        if (isRefresh) {
            params.put("since_id", sinceId);
        } else {
            params.put("since_id", 1);
        }
        twitterClient.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (isRefresh) {
                    tweets.addAll(0, Tweet.fromJSONArray(response));
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    tweets.addAll(Tweet.fromJSONArray(response));

                }
                maxId = tweets.get(tweets.size() - 1).getTweetId() - 1;
                sinceId = tweets.get(0).getTweetId();
                recyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.v("failure", errorResponse.toString());
            }


        }, params);
    }


    @Override
    public void onTweet(RequestParams params) {

        twitterClient.tweetStatus(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                populateTimeline(true, true);
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               // Log.v("failure", errorResponse.toString());

            }
        }, params);
    }
}
