package com.InstaDownload.stories.profile.post.download.models;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MyBridge {
    private static String mUsername;
    private static String mPassword;
    Context mContext;

    public MyBridge(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void saveData(String username, String password) {
        Toast.makeText(mContext, username + " " + password, Toast.LENGTH_SHORT).show();
        mUsername = username;
        mPassword = password;
    }

    public static String getUsername() {
        return mUsername;
    }

    public static String getPassword() {
        return mPassword;
    }
}