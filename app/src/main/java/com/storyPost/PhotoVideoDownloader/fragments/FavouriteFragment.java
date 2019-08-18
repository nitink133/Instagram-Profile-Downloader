package com.storyPost.PhotoVideoDownloader.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.FaveListAdapter;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends Fragment {
    private String TAG=StoriesOverview.class.getName();
    private List<UserObject> userObjectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FaveListAdapter adapter;
    private LinearLayout noNet, noFaves;
    private ImageButton noNetRefresh;
    private SpinKitView wave;
    private SwipeRefreshLayout refreshLayout;
    private TextView text1, text2;
    private Context context;
    private AdView adView;
    private com.facebook.ads.AdView adFbView;
    LinearLayout adContainer;
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
        adContainer = view.findViewById(R.id.adContainer);
        showBannerAd();
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
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
        if (adView != null) {
            adView.pause();
        }

    }


    private void showBannerAd() {
        try {
            adView = new AdView(getActivity());
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
            adFbView = new com.facebook.ads.AdView(getActivity(), getString(R.string.facebook_banner), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
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