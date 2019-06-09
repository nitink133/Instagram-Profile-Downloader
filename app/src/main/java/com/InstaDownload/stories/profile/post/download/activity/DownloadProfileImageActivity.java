package com.InstaDownload.stories.profile.post.download.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.InstaDownload.stories.profile.post.download.view.BoldButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import cz.msebera.android.httpclient.util.TextUtils;

import com.InstaDownload.stories.profile.post.download.ApiUtils;
import com.InstaDownload.stories.profile.post.download.R;
import com.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.InstaDownload.stories.profile.post.download.utils.ToastUtils;
import com.InstaDownload.stories.profile.post.download.utils.ZoomstaUtil;
import com.InstaDownload.stories.profile.post.download.view.RegularButton;
import com.InstaDownload.stories.profile.post.download.view.RegularEditText;

public class DownloadProfileImageActivity extends AppCompatActivity {
    RegularEditText username;
    BoldButton search, openInstagram;
    ProgressBar progress_bar;
    Context context;
    Bitmap bitmap;
    String usernameText;
    ImageButton back;
    ImageView information, history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_download_profile_image);
        username = findViewById(R.id.username);
        search = findViewById(R.id.search);
        progress_bar = findViewById(R.id.progress_bar);
        back = findViewById(R.id.back);

        openInstagram = findViewById(R.id.openInstagram);
        information = findViewById(R.id.information);
        history = findViewById(R.id.history);

        bannerAd();

        history.setOnClickListener(v -> {
            startActivity(new Intent(DownloadProfileImageActivity.this, DownloadHistoryActivity.class));
        });
        information.setOnClickListener(v -> {
            startActivity(new Intent(DownloadProfileImageActivity.this, HowToUseActivity.class));
        });

        search.setOnClickListener(v -> {
            if (TextUtils.isEmpty(username.getText().toString()))
                ToastUtils.ErrorToast(context, "Username field can't be empty");
            else {
                usernameText = username.getText().toString();
                progress_bar.setVisibility(View.VISIBLE);
                new RequestInstagramAPI(ApiUtils.getUsernameUrl(usernameText)).execute();
            }

        });


        openInstagram.setOnClickListener(v -> {
            String _username = username.getText().toString();
            if (TextUtils.isEmpty(_username)) {
                ToastUtils.ErrorToast(context, "Username field can't be empty");
            } else {
                final String appLink = "http://instagram.com/_u/" + _username;
                final String webLink = "http://instagram.com/" + _username;

                Uri uri = Uri.parse(appLink);
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webLink)));

            }
        });

        back.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private AdView adView, mAdView;

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (adView != null) {
            adView.resume();
        }

    }


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (adView != null) {
            adView.resume();
        }
        super.onDestroy();
    }

    public void bannerAd() {
        mAdView = (AdView) findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(DownloadProfileImageActivity.this, "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(DownloadProfileImageActivity.this, "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        String url = "";

        public RequestInstagramAPI(String url) {
            this.url = url + "?__a=1";
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet httpGet = new HttpGet(url);
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

                    progress_bar.setVisibility(View.GONE);

                    ToastUtils.ErrorToast(context, context.getString(R.string.some_error));
                    e.printStackTrace();
                }

            } else {

                progress_bar.setVisibility(View.GONE);
                ToastUtils.ErrorToast(context, context.getString(R.string.some_error));

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


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bitmap != null) {
                        progress_bar.setVisibility(View.GONE);

                        Intent intent = new Intent(DownloadProfileImageActivity.this, ViewProfileActivity.class);
                        intent.putExtra("username", usernameText);
                        ZoomstaUtil.createImageFromBitmap(DownloadProfileImageActivity.this, bitmap);
                        DownloadProfileImageActivity.this.startActivity(intent);
                    } else {
                        ToastUtils.ErrorToast(context, context.getString(R.string.some_error));

                    }
                }


            });


        }
    }
}
