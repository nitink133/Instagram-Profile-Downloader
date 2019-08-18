package com.storyPost.PhotoVideoDownloader.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.storyPost.PhotoVideoDownloader.BuildConfig;
import com.storyPost.PhotoVideoDownloader.R;

public class AppRater {

    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
//        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
//        if (date_firstLaunch == 0) {
//            date_firstLaunch = System.currentTimeMillis();
//            editor.putLong("date_firstlaunch", date_firstLaunch);
//        }

        // Wait at least n days before opening
        if (launch_count > LAUNCHES_UNTIL_PROMPT) {
            editor.putLong("launch_count", 0);
            showRateDialog(mContext, editor);

        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
        View v = layoutInflaterAndroid.inflate(R.layout.item_rate_us_dialog, null);
        android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(mContext);
        alertDialogBuilderUserInput.setView(v);
        final android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        alertDialogAndroid.setCancelable(false);

        TextView text = v.findViewById(R.id.text);
        text.setText("If you enjoy using " + mContext.getString(R.string.app_name) + ", please take a moment to rate it. Thanks for your support!");
        Button remindlater, never, rateus;
        remindlater = v.findViewById(R.id.remindlater);
        never = v.findViewById(R.id.never);
        rateus = v.findViewById(R.id.rateus);


        rateus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                alertDialogAndroid.dismiss();
            }
        });
        remindlater.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
            }
        });


        never.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                alertDialogAndroid.dismiss();
            }
        });


    }
}