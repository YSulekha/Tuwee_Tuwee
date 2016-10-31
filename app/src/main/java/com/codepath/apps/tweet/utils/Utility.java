package com.codepath.apps.tweet.utils;


import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.loopj.android.http.AsyncHttpClient.log;

//Utility class
public class Utility {


    public static String relativeTime(String createdDate) {
        //Sun Oct 30 03:00:01 +0000 2016
        String createdDateFormat = "EEE MMM dd HH:mm:ss ZZZZ yyyy";
        SimpleDateFormat format = new SimpleDateFormat(createdDateFormat, Locale.ENGLISH);
        format.setLenient(true);
        String relativeDate = "";

        try {
            long dateInms = format.parse(createdDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateInms, System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            Log.d("Error formating date","Utility");
            e.printStackTrace();
        }
        relativeDate = relativeDate.replace("ago","");
        if(relativeDate.contains("minute")){
            relativeDate = relativeDate.replace("minutes","minute");
            relativeDate = relativeDate.replace("minute","m");
            log.v("response",relativeDate);
        }
        else if(relativeDate.contains("second")){
            relativeDate = relativeDate.replace("seconds","second");
            relativeDate = relativeDate.replace("second","s");
        }
        else if(relativeDate.contains("hour")){
            relativeDate = relativeDate.replace("hours","hour");
            relativeDate= relativeDate.replace("hour","h");
        }
        else if(relativeDate.contains("day")){
            relativeDate= relativeDate.replace("days","d");
            relativeDate= relativeDate.replace("day","d");

        }
        relativeDate = relativeDate.replace(" ","");
        return relativeDate;
    }
}
