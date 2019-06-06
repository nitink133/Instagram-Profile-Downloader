package com.blackpaper.InstaDownload.stories.profile.post.download.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.ApiUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.activity.ViewProfileActivity;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.blackpaper.InstaDownload.stories.profile.post.download.models.UserObject;

/**
 * Created by tirgei on 11/1/17.
 */

public class OverviewDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private String image;
    private String username;
    private Button openInsta;
    private ImageView imageView;
    private TextView userText;
    private String link = null;
    private Bitmap bitmap = null;
    private ProgressBar progressBar;
    private ImageButton imageButton;
    private String userId;
    private Boolean isFave = false;
    private UserObject object;
    private Boolean isMe = false;

    public OverviewDialog(Activity activity, Bitmap bitmap, String username, Boolean isMe) {
        super(activity);

        this.activity = activity;
        this.bitmap = bitmap;
        this.username = username;
        this.isMe = isMe;
    }

    public OverviewDialog(Activity activity, UserObject userObject) {
        super(activity);

        this.activity = activity;
        this.object = userObject;
        this.image = userObject.getImage();
        this.username = userObject.getUserName();
        this.isFave = userObject.getFaved();
        this.userId = userObject.getUserId();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_prof_overview);

        imageView = findViewById(R.id.overview_image);
        openInsta = findViewById(R.id.overview_button);
        userText = findViewById(R.id.overview_username);
        progressBar = findViewById(R.id.loading_overview_image);
        imageButton = findViewById(R.id.fave_user_button);
        if (isFave && !isMe)
            imageButton.setImageResource(R.drawable.ic_bookmark_selected);
        else if (!isMe && !isFave)
            imageButton.setImageResource(R.drawable.ic_bookmark_unselected);
        else if (isMe)
            imageButton.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        new RequestInstagramAPI(ApiUtils.getUsernameUrl(username)).execute();
        if (!isMe)
            Glide.with(activity).load(image).thumbnail(0.2f).into(imageView);
        else
            imageView.setImageBitmap(bitmap);

        userText.setText(username);

        int count = ZoomstaUtil.getIntegerPreference(activity, "profCount");
        count++;
        if (count < 2822)
            ZoomstaUtil.setIntegerPreference(activity, count, "profCount");
        else
            ZoomstaUtil.setIntegerPreference(activity, 1, "profCount");

        openInsta.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        imageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.overview_button:
                final String appLink = "http://instagram.com/_u/" + username;
                final String webLink = "http://instagram.com/" + username;

                Uri uri = Uri.parse(appLink);
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                if (isIntentAvailable(activity, insta)) {
                    activity.startActivity(insta);
                } else {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webLink)));
                }

                break;

            case R.id.fave_user_button:
                if (!isFave) {
                    ZoomstaUtil.addFaveUser(activity, userId);
                    imageButton.setImageResource(R.drawable.ic_bookmark_selected);
                    object.setFaved(true);
                    isFave = true;
                    Toast.makeText(activity, username + " added to favourite", Toast.LENGTH_SHORT).show();

                } else {
                    ZoomstaUtil.removeFaveUser(activity, userId);
                    imageButton.setImageResource(R.drawable.ic_bookmark_unselected);
                    object.setFaved(false);
                    isFave = false;
                    Toast.makeText(activity, username + " removed from favourite", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.overview_image:
                if (bitmap == null)
                    Toast.makeText(activity, "Please wait for the image to load", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        String url = "";

        public RequestInstagramAPI(String url) {
            this.url = url + "?__a=1";
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);


            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    IntagramProfileResponse intagramProfileResponse = new Gson().fromJson(response, IntagramProfileResponse.class);
                    if (intagramProfileResponse.getGraphql() != null && intagramProfileResponse.getGraphql().getUser() != null) {
                        final String newLink = intagramProfileResponse.getGraphql().getUser().getProfilePicUrlHd();

                        new RequestProfileInstagramAPI(newLink).execute();

                    }

                } catch (JSONException e) {

                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            } else {

                progressBar.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getContext(), getContext().getString(R.string.some_error), Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class RequestProfileInstagramAPI extends AsyncTask<Void, String, String> {

        String url = "";
        HttpURLConnection connection = null;

        public RequestProfileInstagramAPI(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            connection = null;
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.connect();
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);


            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
                        imageView.setImageBitmap(null);
                        imageView.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (bitmap != null) {
                                    Intent intent = new Intent(activity, ViewProfileActivity.class);
                                    intent.putExtra("username",username);
                                    intent.putExtra("user_id",userId);
                                    ZoomstaUtil.createImageFromBitmap(activity, bitmap);
                                    activity.startActivity(intent);
                                }
                            }
                        });

                    }


                    link = null;
                }
            });


        }
    }
}
