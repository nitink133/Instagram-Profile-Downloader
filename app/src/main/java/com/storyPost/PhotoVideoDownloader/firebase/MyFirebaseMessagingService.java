package com.storyPost.PhotoVideoDownloader.firebase;


import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.storyPost.PhotoVideoDownloader.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    Context context;
    private String eid = "";
    private String titlee = "";


    String notificationType = "";
    String notificationId = "";

    String message = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        context = getApplicationContext();
        if (remoteMessage == null)
            return;

        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

        }

        Log.e("remoteMessage.getData", " >> "+remoteMessage.getData().size());

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

    }

    private void handleDataMessage(JSONObject data) {
        Log.e(TAG, "push json: " + data.toString());

        try {

            notificationType = data.getString("type");
            notificationId = data.getString("item_id");
            message = data.getString("message");

            Log.d("testingnotifyoreo", "notificationId = " + notificationId + "  notificationType  =" + notificationType);

            String imageUrl = "";
            String timestamp = String.valueOf(System.currentTimeMillis() / 100);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
// app is in foreground, broadcast the push message
                Intent pushNotification = null;

                if (notificationType.equals("")) {
//                    pushNotification = new Intent(this, Notificationget.class);
//                    pushNotification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {
//                    pushNotification = new Intent(this, View_details_Search_items.class);
//                    pushNotification.putExtra("notificationType", notificationType);
//                    pushNotification.putExtra("notificationId", notificationId);
//                    pushNotification.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }


                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                showNotificationMessage(getApplicationContext(), "VAG", message, timestamp, pushNotification);

            } else {
                Intent resultIntent = null;

                if (notificationType.equals("")) {
//                    resultIntent = new Intent(this, Notificationget.class);
//                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {
//                    resultIntent = new Intent(this, View_details_Search_items.class);
//                    resultIntent.putExtra("notificationType", notificationType);
//                    resultIntent.putExtra("notificationId", notificationId);
//                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }

// app is in background, show the notification in notification tray
              /*  Intent resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                resultIntent.putExtra("notification_type", notification_type);
                resultIntent.putExtra("order_id", order_id);
*/

                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), "VAG", message, timestamp, resultIntent);
                } else {
// image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), "VAG", message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }


    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


}
