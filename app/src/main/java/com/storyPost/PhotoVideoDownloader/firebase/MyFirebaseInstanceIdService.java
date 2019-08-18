package com.storyPost.PhotoVideoDownloader.firebase;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by nitin on 11/9/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseMessagingService {
    private final static String TAG = MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
    }
}