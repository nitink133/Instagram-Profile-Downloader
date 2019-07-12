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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.StoriesListAdapter;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.editText_search)
    public EditText mEdtSearch;
    @BindView(R.id.tv_no_results)
    public TextView mTxvNoResultsFound;
    @BindView(R.id.swipe_refresh_layout_search)
    public SwipeRefreshLayout mSwipeRefreshSearch;
    @BindView(R.id.search_recycler_view)
    public RecyclerView recyclerView;
    private StoriesListAdapter storiesListAdapter;
    private List<UserObject> userObjectList;

    private AdView adView,mAdView;
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

    public void bannerAd(){
        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(SearchActivity.this, "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(SearchActivity.this, "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        createToolbar();
        initViews();
        bannerAd();
        userObjectList = new ArrayList<>();

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
            storiesListAdapter.setUserObjects(userObjectList);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                userObjectList.clear();
                userObjectList.addAll(InstaUtils.searchUser(SearchActivity.this, searchedQuery));


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            storiesListAdapter.setUserObjects(userObjectList);
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

}

