package com.codepath.apps.tweet.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.tweet.R;
import com.codepath.apps.tweet.activities.TimelineActivity;
import com.codepath.apps.tweet.databinding.DialogTweetBinding;


public class ComposeDialog extends DialogFragment {

    private DialogTweetBinding binding;
    String status;
   private SaveFilterListener listener;

    public ComposeDialog(){

    }
    public interface SaveFilterListener{
        void onTweet(String status);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ComposeDialog newInstance(Bundle args){
        ComposeDialog cd = new ComposeDialog();
        return cd;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       //return inflater.inflate(R.layout.dialog_tweet,container);
       binding =  DataBindingUtil.inflate(inflater,R.layout.dialog_tweet,container,false);
        binding.setHandlers(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         status = binding.dialogStatus.getText().toString();

    }

    public void onClick(View v){
        listener = (TimelineActivity)getActivity();
        status = binding.dialogStatus.getText().toString();
        listener.onTweet(status);
        dismiss();
    }
}
