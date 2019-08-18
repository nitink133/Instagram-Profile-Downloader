package com.storyPost.PhotoVideoDownloader.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.StoryViewPagerAdapter;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;
import com.storyPost.PhotoVideoDownloader.utils.DataHolder;
import com.storyPost.PhotoVideoDownloader.utils.StoryListener;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

import java.util.ArrayList;
import java.util.List;

public class ViewStoryActivity extends BaseActivity implements ViewPager.OnPageChangeListener, StoryListener {
    private String TAG = ViewStoryActivity.class.getName();
    private ViewPager viewPager;
    private StoryViewPagerAdapter adapter;
    private ArrayList<StoryModel> modelList;
    private int position = 0, totalPages;
    private List<String> urls;
    private Boolean isFromNet;
    private int count;
    boolean isDownloadPostImage, isAlreadyDownloaded, isFromDownloadScreen;
    private AdView adView;
    private com.facebook.ads.AdView adFbView;
    private LinearLayout adContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_story);
        adContainer = findViewById(R.id.adContainer);
        showBannerAd();
        viewPager = findViewById(R.id.story_view_pager);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);


        modelList = new ArrayList<>();
        urls = new ArrayList<>();
        if (getIntent() != null) {
            isDownloadPostImage = getIntent().getBooleanExtra("isDownloadPostImage", false);
            isAlreadyDownloaded = getIntent().getBooleanExtra("isAlreadyDownloaded", false);
            isFromDownloadScreen = getIntent().getBooleanExtra("isFromDownloadScreen", false);

        }

        loadStories();
        adapter = new StoryViewPagerAdapter(getSupportFragmentManager(), modelList, isFromNet, urls, isDownloadPostImage, isAlreadyDownloaded, isFromDownloadScreen);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(position);


    }

    private void loadStories() {
        isFromNet = getIntent().getBooleanExtra("isFromNet", false);
        Boolean isLarge = getIntent().getBooleanExtra("isLarge", false);
        if (!isFromNet) {
            if (isLarge) {
                Log.d("data", "Receiving large data...");
                modelList = (ArrayList<StoryModel>) DataHolder.getData();

            } else {
                modelList = getIntent().getParcelableArrayListExtra("storylist");
                Log.d("data", "Data received is small");

            }
            position = getIntent().getIntExtra("pos", 0);
            totalPages = modelList.size();
        } else {
            urls = getIntent().getStringArrayListExtra("urls");
            position = getIntent().getIntExtra("pos", 0);
            totalPages = urls.size();
        }

        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        count++;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    InterstitialAd interstitialAd = null;




    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.enter_signin, R.anim.exit_main);
        }

    }

    @Override
    public void deletePage(Boolean delete) {
        if (delete)
            adapter.deletePage(viewPager.getCurrentItem());

        if (viewPager.getChildCount() == 0) {
            ZoomstaUtil.setBooleanPreference(this, "refreshSaved", true);
            finish();
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
            adView.setAdSize(AdSize.SMART_BANNER);
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
            adFbView = new com.facebook.ads.AdView(this, getString(R.string.facebook_banner), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
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
}
