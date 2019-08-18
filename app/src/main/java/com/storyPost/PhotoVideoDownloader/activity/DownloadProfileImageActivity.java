package com.storyPost.PhotoVideoDownloader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.storyPost.PhotoVideoDownloader.ApiUtils;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.data.retrofit.response.IntagramProfileResponse;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;
import com.storyPost.PhotoVideoDownloader.view.BoldButton;
import com.storyPost.PhotoVideoDownloader.view.RegularEditText;

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

public class DownloadProfileImageActivity extends BaseActivity {
    private String TAG = DownloadProfileImageActivity.class.getName();
    RegularEditText username;
    BoldButton search, openInstagram;
    ProgressBar progress_bar;

    public Toolbar toolbar;
    Context context;
    Bitmap bitmap;
    String usernameText;
    ImageButton back;
    ImageView information, history;
    private AdView adView;
    private com.facebook.ads.AdView adFbView;
    LinearLayout adContainer;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd mInterstitialFbAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_download_profile_image);
        username = findViewById(R.id.username);
        search = findViewById(R.id.search);
        progress_bar = findViewById(R.id.progress_bar);
        back = findViewById(R.id.back);
        adContainer = findViewById(R.id.adContainer);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.lbl_how_to_use));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.lbl_download_profile_image));

        loadFullscreenAd();
        loadFbFullscreenAd();
        showBannerAd();
        openInstagram = findViewById(R.id.openInstagram);
        information = findViewById(R.id.information);
        history = findViewById(R.id.history);
        history.setOnClickListener(v -> {
            startActivity(new Intent(DownloadProfileImageActivity.this, DownloadHistoryActivity.class));
        });
        information.setOnClickListener(v -> {
            startActivity(new Intent(DownloadProfileImageActivity.this, HowToUseActivity.class));
        });

        search.setOnClickListener(v -> {
            if (TextUtils.isEmpty(username.getText().toString()))
                ToastUtils.ErrorToast(context, "Username field can't be empty");
            else if (username.getText().toString().contains(" "))
                ToastUtils.ErrorToast(context, "Invalid Username");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.history:
                startActivity(new Intent(DownloadProfileImageActivity.this, DownloadHistoryActivity.class));
                return true;
            case R.id.information:
                startActivity(new Intent(DownloadProfileImageActivity.this, HowToUseActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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
            } catch (Exception e) {
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

                } catch (Exception e) {

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


    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {

        if (adView != null) {
            adView.destroy();
        }
        if (adFbView != null) {
            adFbView.destroy();
        }

        if (mInterstitialFbAd != null) {
            mInterstitialFbAd.destroy();
        }
        super.onDestroy();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }

    }


    private void showBannerAd() {
        try {
            adView = new AdView(this);
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId(getString(R.string.banner_home_footer));
            adContainer.addView(adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    adContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    showFbBannerAd();
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "showBannerAd: " + e);
        }
    }


    private void showFbBannerAd() {
        try {
            adFbView = new com.facebook.ads.AdView(this, getString(R.string.facebook_rectangle), com.facebook.ads.AdSize.RECTANGLE_HEIGHT_250);
            adContainer.removeAllViews();
            adContainer.addView(adFbView);
            adFbView.loadAd();
            adFbView.setAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    adContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    adContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "showBannerAd: " + e);
        }
    }


    private void loadFullscreenAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .build());
    }

    private void loadFbFullscreenAd() {
        mInterstitialFbAd = new com.facebook.ads.InterstitialAd(DownloadProfileImageActivity.this, getString(R.string.facebook_interstitial));
        mInterstitialFbAd.loadAd();

    }


    public void showFullScreenAds() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else if (mInterstitialFbAd.isAdLoaded()) {
            mInterstitialFbAd.show();
        }
    }

    @Override
    public void onBackPressed() {
        showFullScreenAds();
        super.onBackPressed();
    }
}
