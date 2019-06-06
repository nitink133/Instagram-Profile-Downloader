package com.blackpaper.InstaDownload.stories.profile.post.download.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.adapter.StoriesListAdapter;
import com.blackpaper.InstaDownload.stories.profile.post.download.models.UserObject;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.InstaUtils;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        createToolbar();
        initViews();
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
        storiesListAdapter = new StoriesListAdapter(this);
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

