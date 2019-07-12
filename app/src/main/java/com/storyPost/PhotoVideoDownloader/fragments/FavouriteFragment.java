package com.storyPost.PhotoVideoDownloader.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.FaveListAdapter;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;


public class FavouriteFragment extends Fragment {

    private List<UserObject> userObjectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FaveListAdapter adapter;
    private LinearLayout noNet, noFaves;
    private ImageButton noNetRefresh;
    private SpinKitView wave;
    private SwipeRefreshLayout refreshLayout;
    private TextView text1, text2;
    private Context context;

    public FavouriteFragment() {
        // Required empty public constructor
    }

    private class GetStoriesFeed extends AsyncTask<Boolean, String, String> {
        private String response;

        private GetStoriesFeed() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!refreshLayout.isRefreshing())
                wave.setVisibility(View.VISIBLE);
            if (noNet.isShown())
                noNet.setVisibility(View.GONE);
            if (!userObjectList.isEmpty())
                userObjectList.clear();
            adapter.setUserObjects(userObjectList);
        }

        @Override
        protected String doInBackground(Boolean... booleans) {
            try {
//                userObjectList.addAll(InstaUtils.favesList(getActivity()));
                userObjectList.addAll(ZoomstaUtil.getFav(context));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (userObjectList.size() == 0) {
                text1.setText(R.string.no_fave_stories);
                text2.setText(R.string.refresh);
                noFaves.setVisibility(View.VISIBLE);
            } else
                adapter.setUserObjects(userObjectList);

            wave.setVisibility(View.GONE);
            if (refreshLayout.isRefreshing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
            if (noFaves.isShown())
                noFaves.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);

        }
    }

    private AdView mAdView;

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

    }

    public void bannerAd(View v) {
        mAdView = (AdView) v.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .build();


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getActivity(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                setMargins(refreshLayout, 0, 0, 0, 0);
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLeftApplication() {

                Toast.makeText(getActivity(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        Log.d("FaveStoriesFragment", "Loading stories");
        recyclerView = view.findViewById(R.id.fave_users_stories_rv);
        wave = view.findViewById(R.id.loading_fave_stories);
        noNet = view.findViewById(R.id.no_net_faves);
        noFaves = view.findViewById(R.id.no_faves);
        refreshLayout = view.findViewById(R.id.refresh_faves);
        noNetRefresh = view.findViewById(R.id.refresh_fave_stories_button);
        text1 = view.findViewById(R.id.fave_text1);
        text2 = view.findViewById(R.id.fave_text2);
        bannerAd(view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FaveListAdapter(getActivity());
        adapter.setUserObjects(userObjectList);
        recyclerView.setAdapter(adapter);

        loadStories();

        noNetRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadStories();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!userObjectList.isEmpty())
                    userObjectList.clear();

                loadStories();
            }
        });


        return view;
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        t = (int) styledAttributes.getDimension(0, 0);

        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void loadStories() {
        if (ZoomstaUtil.haveNetworkConnection(getActivity())) {

            if (ZoomstaUtil.getUsers(getActivity()) != null)
                new GetStoriesFeed().execute(new Boolean[]{Boolean.FALSE});
            else {
                noFaves.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                if (refreshLayout.isRefreshing()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                        }
                    }, 1500);
                }

            }

        } else {
            noNet.setVisibility(View.VISIBLE);
            if (refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);

    }


}
