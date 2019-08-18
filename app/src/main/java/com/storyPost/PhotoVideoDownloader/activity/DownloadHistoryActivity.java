package com.storyPost.PhotoVideoDownloader.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.tabs.TabLayout;
import com.snatik.storage.Storage;

import java.util.ArrayList;
import java.util.List;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.DownloadStoriesoverViewAdapter;
import com.storyPost.PhotoVideoDownloader.adapter.ViewPagerAdapter;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Downloads;
import com.storyPost.PhotoVideoDownloader.fragments.DownloadHistoryPhotoFragment;
import com.storyPost.PhotoVideoDownloader.fragments.DownloadHistoryVideoFragment;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.RecyclerItemClickListener;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;
import com.storyPost.PhotoVideoDownloader.view.BoldTextView;
import com.storyPost.PhotoVideoDownloader.view.RegularTextView;

public class DownloadHistoryActivity extends BaseActivity {


    private Window window;
    private LinearLayout.LayoutParams params;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout llContainer;
    private ViewPagerAdapter viewPagerAdapter;

    private Toolbar toolbar;
    private AdView adView;
    private com.facebook.ads.AdView adFbView;
    LinearLayout adContainer;
    private String TAG = DownloadFromUrlActivity.class.getName();
    private com.facebook.ads.InterstitialAd mInterstitialFbAd;
    private InterstitialAd mInterstitialAd;
    private Menu menu;
    private DownloadHistoryPhotoFragment downloadHistoryPhotoFragment;
    private DownloadHistoryVideoFragment downloadHistoryVideoFragment;
    private boolean isPhotoFragmentVisible = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_download_history);

        findViewById();
        init();


        loadFullscreenAd();
        loadFbFullscreenAd();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.lbl_how_to_use));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.lbl_download_history));

        adContainer = findViewById(R.id.adContainer);
        showBannerAd();

    }

    private void findViewById() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        llContainer = findViewById(R.id.llContainer);
    }



    private void init() {
        Rect displayRectangle = new Rect();
        window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        downloadHistoryPhotoFragment = new DownloadHistoryPhotoFragment();
        downloadHistoryVideoFragment = new DownloadHistoryVideoFragment();

        downloadHistoryPhotoFragment.setEventListener(new DownloadHistoryPhotoFragment.EventListener() {
            @Override
            public void multi_select(int position) {

            }

            @Override
            public void updateActionBar(boolean isVisible, int count) {
                if (menu != null) {
                    MenuItem item = menu.findItem(R.id.action_delete);
                    item.setVisible(isVisible);
                }

                if (count != -1)
                    toolbar.setTitle(count + " items selected");
                else toolbar.setTitle("Download History");
            }

            @Override
            public void isPhotoFragmentVisible(boolean isVisible) {

            }
        });

        downloadHistoryVideoFragment.setEventListener(new DownloadHistoryVideoFragment.EventListener() {
            @Override
            public void multi_select(int position) {

            }

            @Override
            public void updateActionBar(boolean isVisible, int count) {
                if (menu != null) {
                    MenuItem item = menu.findItem(R.id.action_delete);
                    item.setVisible(true);
                }

                if (count != -1)
                    toolbar.setTitle(count + " items selected");
                else toolbar.setTitle("Download History");
            }

            @Override
            public void isPhotoFragmentVisible(boolean isVisible) {
//                isPhotoFragmentVisible = isVisible;
            }
        });
        viewPagerAdapter.addFragment(downloadHistoryPhotoFragment, "Images");
        viewPagerAdapter.addFragment(downloadHistoryVideoFragment, "Videos");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0: isPhotoFragmentVisible=true;
                    break;
                    case 1: isPhotoFragmentVisible=false;
                    break;
                    default:
                        isPhotoFragmentVisible=true;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_download_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_delete:

                if (isPhotoFragmentVisible)
                    downloadHistoryPhotoFragment.showAlertDialog();
                else downloadHistoryVideoFragment.showAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);

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
            adView.setAdSize(AdSize.BANNER);
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
                    downloadHistoryPhotoFragment.setMargins();
                    downloadHistoryVideoFragment.setMargins();
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
        mInterstitialFbAd = new com.facebook.ads.InterstitialAd(DownloadHistoryActivity.this, getString(R.string.facebook_interstitial));
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
    public void setMargins(View v, int l, int t, int r, int b) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
//        t = (int) styledAttributes.getDimension(0, 0);

        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}

