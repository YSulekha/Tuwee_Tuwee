package com.codepath.apps.tweet.adapters;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweet.R;
import com.codepath.apps.tweet.databinding.ContentItemBinding;
import com.codepath.apps.tweet.models.Tweet;
import com.codepath.apps.tweet.models.User;
import com.codepath.apps.tweet.utils.Utility;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

//Array Adapter to populate recyclerView
public class TweetsArrayAdapter extends RecyclerView.Adapter<TweetsArrayAdapter.ViewHolder>{

    private ArrayList<Tweet> tweets;
    private static Context mContext;

    public TweetsArrayAdapter(Context context, ArrayList<Tweet> results){
        this.mContext = context;
        this.tweets = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.content_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        User user = tweet.getUser();

        holder.binding.setTweet(tweet);
        holder.binding.setUser(user);
        holder.binding.executePendingBindings();

    }
    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String url){
        //    Picasso.with(imageView.getContext()).load(url).placeholder(R.color.colorPrimary).
        //      // into(imageView);
        Glide.with(mContext).load(url).bitmapTransform(new RoundedCornersTransformation(imageView.getContext(),8,0)).
                into(imageView);
    }

    @BindingAdapter("bind:timeCalc")
    public static void timeCalc(TextView textView, String createdAt){
        String relativeDate = Utility.relativeTime(createdAt);
        Log.v("response",relativeDate);
        textView.setText(relativeDate);
    }
    @BindingAdapter("bind:videoDisplay")
    public static void videoDisplay(ScalableVideoView view, String url){
        if(url!=null) {
            Uri uri = Uri.parse(url);
            try {
                view.setDataSource(view.getContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ContentItemBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
