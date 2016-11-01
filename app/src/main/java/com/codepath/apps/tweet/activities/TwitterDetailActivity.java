package com.codepath.apps.tweet.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweet.R;
import com.codepath.apps.tweet.databinding.ActivityTwitterDetailBinding;
import com.codepath.apps.tweet.fragments.ComposeDialog;
import com.codepath.apps.tweet.models.Tweet;
import com.codepath.apps.tweet.utils.Utility;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TwitterDetailActivity extends AppCompatActivity {

    ActivityTwitterDetailBinding binding;
    private ComposeDialog.SaveFilterListener listener;
    Tweet t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("DetailA","af");
        binding = DataBindingUtil.setContentView(this,R.layout.activity_twitter_detail);

        t = (Tweet)Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        binding.detailBody.setText(t.getBody());
        binding.detailName.setText(t.getUser().getUserName());
        binding.detailScreenname.setText(t.getUser().getScreenName());
        Glide.with(this).load(t.getUser().getImageUrl()).into(binding.detailProfile);
        binding.detailTime.setText(timeCalc(t.getCreatedAt()));
        if(t.getMediaUrl() !=null){
            Glide.with(this).load(t.getMediaUrl()).bitmapTransform(new RoundedCornersTransformation(this,8,0)).into(binding.detailMedia);
        }
        else{
            binding.detailMedia.setImageResource(0);
        }

    }

    public void onClickReply(View v){
        FragmentManager fm = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putBoolean("reply",true);
        args.putString("userId",t.getUser().getScreenName());
        args.putLong("tweetId",t.getTweetId());
        ComposeDialog dialog = ComposeDialog.newInstance(args);
        dialog.show(fm, "Dialog");
    }

    public static String timeCalc(String createdAt){

        //"created_at":"Mon Oct 31 14:20:57 +0000 2016"
        String relativeDate = Utility.relativeTime(createdAt);
        String d = Utility.formatDate(createdAt);
        Log.v("responseDDe",d);
        return d;

    }
    public static void loadImage(ImageView imageView, String url){
        //    Picasso.with(imageView.getContext()).load(url).placeholder(R.color.colorPrimary).
        //      // into(imageView);
        Log.v("Detail",url);
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }
}
