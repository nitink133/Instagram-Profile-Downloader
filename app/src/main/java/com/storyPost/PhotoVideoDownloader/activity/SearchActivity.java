package com.storyPost.PhotoVideoDownloader.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.StoriesListAdapter;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SearchActivity extends BaseActivity {
    private static final String TAG =SearchActivity.class.getName() ;
    @BindView(R.id.editText_search)
    public EditText mEdtSearch;
    @BindView(R.id.tv_no_results)
    public TextView mTxvNoResultsFound;
    @BindView(R.id.swipe_refresh_layout_search)
    public SwipeRefreshLayout mSwipeRefreshSearch;
    @BindView(R.id.search_recycler_view)
    public RecyclerView recyclerView;
    private StoriesListAdapter storiesListAdapter;
    private List<UserObject> userObjectListCopy;
    private List<Object> userObjectList;
    private AdView adView;
    private com.facebook.ads.AdView adFbView;
    LinearLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        adContainer = findViewById(R.id.adContainer);
        showBannerAd();
        createToolbar();
        initViews();

        userObjectList = new ArrayList<>();
        userObjectListCopy = new ArrayList<>();

//        if (getIntent() != null) {
//            userObjectList = getIntent().getParcelableArrayListExtra("userObjectList");
//            if (userObjectList != null) {
//                storiesListAdapter.setUserObjects(userObjectList);
//            }
//        }

        // Add Text Change Listener to EditText
        mEdtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
//                storiesListAdapter.getFilter().filter(s.toString());
                mSwipeRefreshSearch.setRefreshing(true);
                new SearchUser(mEdtSearch.getText().toString()).execute();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEdtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        // clear editText focus and hide
                        mEdtSearch.clearFocus();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);

                        mSwipeRefreshSearch.setRefreshing(true);
                        new SearchUser(mEdtSearch.getText().toString()).execute();

                        break;
                }
                return true;
            }
        });

        mSwipeRefreshSearch.setEnabled(false);
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_24px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                SearchActivity.this.overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initViews() {

        userObjectList = new ArrayList<>();
        userObjectListCopy = new ArrayList<>();
        storiesListAdapter = new StoriesListAdapter(this,false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(storiesListAdapter);
    }


    private void searchEverything(final String search) {
        mSwipeRefreshSearch.setEnabled(true);
        mSwipeRefreshSearch.setRefreshing(true);


    }


    private class SearchUser extends AsyncTask<String, Void, Void> {
        private String searchedQuery;

        private SearchUser(String searchedQuery) {
            this.searchedQuery = searchedQuery;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            userObjectList.clear();
            storiesListAdapter.setUserObjects(userObjectListCopy,userObjectList);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                userObjectList.clear();
                userObjectListCopy.clear();
                userObjectList.addAll(InstaUtils.searchUser(SearchActivity.this, searchedQuery));
                userObjectListCopy.addAll(InstaUtils.searchUser(SearchActivity.this, searchedQuery));


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            storiesListAdapter.setUserObjects(userObjectListCopy,userObjectList);
            mSwipeRefreshSearch.setRefreshing(false);

        }
    }


    private void cancelSearch() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
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
        }catch (Exception e){
            Log.e(TAG, "showBannerAd: "+e );
        }
    }


    private void showFbBannerAd() {
        try {
            adFbView = new com.facebook.ads.AdView(this, getString(R.string.facebook_banner), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
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
        }catch (Exception e){
            Log.e(TAG, "showBannerAd: "+e );
        }
    }

}

