package com.storyPost.PhotoVideoDownloader.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.SearchActivity;
import com.storyPost.PhotoVideoDownloader.adapter.StoriesListAdapter;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

import java.util.ArrayList;
import java.util.List;

public class StoriesFragment extends Fragment {
    private String TAG = StoriesFragment.class.getName();
    private AdView adView;
    private com.facebook.ads.AdView adFbView;
    private LinearLayout adContainer;

    public StoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private List<Object> userObjectList = new ArrayList<>();
    private List<UserObject> userObjectListCopy = new ArrayList<>();
    private List<UserObject> userObjectListForSearching = new ArrayList<>();
    private RecyclerView recyclerView;
    private StoriesListAdapter adapter;
    private SpinKitView wave;
    private LinearLayout noNet;
    private ImageButton noNetRefresh;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout noStories;
    private TextView text1, text2;

/*
        Method to add Native Express Ads to our Original Dataset
    */

    int spaceBetweenAds = 7;
    // The AdLoader used to load ads.
    AdLoader adLoader;
    List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    private void loadNativeAds() {

        AdLoader.Builder builder = new AdLoader.Builder(context, getString(R.string.native_ad_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInMenuItems();
                        }
                    }
                }).build();

        // Load the Native Express ad.
    }

    private void insertAdsInMenuItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        for (UnifiedNativeAd ad: mNativeAds) {
            userObjectList.add(spaceBetweenAds, ad);
        }

        adapter.setUserObjects(userObjectListCopy,userObjectList);
    }
    private void addNativeExpressAds() {
        if (context != null) {



            // List of native ads that have been successfully loaded.
            // We are looping through our original dataset
            // And adding Admob's Native Express Ad at consecutive positions at a distance of spaceBetweenAds
            // You should change the spaceBetweenAds variable according to your need
            // i.e how often you want to show ad in RecyclerView

            for (int i = spaceBetweenAds; i <= userObjectList.size(); i += (spaceBetweenAds + 1)) {
                NativeExpressAdView adView = new NativeExpressAdView(context);
                // I have used a Test ID provided by Admob below
                // you should replace it with yours
                // And if wou are just experimenting, then just copy the code
                adView.setAdUnitId(getString(R.string.native_ad_id));
                userObjectList.add(i, adView);
            }
            NativeExpressAdView adView = new NativeExpressAdView(context);
            // I have used a Test ID provided by Admob below
            // you should replace it with yours
            // And if wou are just experimenting, then just copy the code
            adView.setAdUnitId(getString(R.string.native_ad_id));
            userObjectList.add(adView);

            adapter.setUserObjects(userObjectListCopy, userObjectList);

            // Below we are using post on RecyclerView
            // because we want to resize our native ad's width equal to screen width
            // and we should do it after RecyclerView is created

            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    float scale = context.getResources().getDisplayMetrics().density;
                    int adWidth = (int) (recyclerView.getWidth() - (2 * context.getResources().getDimension(R.dimen.activity_horizontal_margin)));

                    // we are setting size of adView
                    // you should check admob's site for possible ads size
                    AdSize adSize = new AdSize((int) (adWidth / scale), 150);

                    // looping over mDataset to sesize every Native Express Ad to ew adSize
                    for (int i = spaceBetweenAds; i < userObjectList.size(); i += (spaceBetweenAds + 1)) {
                        NativeExpressAdView adViewToSize = (NativeExpressAdView) userObjectList.get(i);
                        adViewToSize.setAdSize(adSize);
                    }

                    // calling method to load native ads in their views one by one
                    loadNativeExpressAd(spaceBetweenAds);
                }
            });

        }
    }


    /*
        Loads the Native Express ads in the items list.
        Here we are loading next ad after previous ad has finished loading
        so that it does not throw an error and blocks our UI
    */

    private void loadNativeExpressAd(final int index) {

        if (index >= userObjectList.size()) {
            return;
        }

        Object item = userObjectList.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a Native"
                    + " Express ad.");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        // Set an AdListener on the NativeExpressAdView to wait for the previous Native Express ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous Native Express ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadNativeExpressAd(index + spaceBetweenAds + 1);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous Native Express ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("AdmobMainActivity", "The previous Native Express ad failed to load. Attempting to"
                        + " load the next Native Express ad in the items list.");
                loadNativeExpressAd(index + spaceBetweenAds + 1);
            }
        });

        // Load the Native Express ad.
        //We also registering our device as Test Device with addTestDevic("ID") method
        adView.loadAd(new AdRequest.Builder().addTestDevice("ca-app-pub-3940256099942544/2247696110").build());
    }


    @Override
    public void onStart() {
        super.onStart();
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
            if (!userObjectList.isEmpty()) {
                userObjectList.clear();
                userObjectListCopy.clear();
                userObjectListForSearching.clear();
            }
//            addNativeExpressAds();
            adapter.setUserObjects(userObjectListCopy,userObjectList);
        }

        @Override
        protected String doInBackground(Boolean... booleans) {
            try {
                userObjectList.addAll(InstaUtils.usersList(getActivity()));
                userObjectListCopy.addAll(InstaUtils.usersList(getActivity()));

                userObjectListForSearching.addAll(userObjectListCopy);

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
                noStories.setVisibility(View.VISIBLE);
            } else {
//                adapter.setUserObjects(userObjectListCopy,userObjectList);
//                addNativeExpressAds();
                adapter.setUserObjects(userObjectListCopy,userObjectList);

                adLoader.loadAds(new AdRequest.Builder().build(), userObjectListCopy.size()/7);
            }


            wave.setVisibility(View.GONE);
            if (refreshLayout.isRefreshing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }

            if (noStories.isShown())
                noStories.setVisibility(View.GONE);

            if (!recyclerView.isShown())
                recyclerView.setVisibility(View.VISIBLE);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stories, container, false);

        Log.d("StoriesFragment", "Loading stories");

        recyclerView = view.findViewById(R.id.stories_rv);
        wave = view.findViewById(R.id.loading_stories);
        noNet = view.findViewById(R.id.no_net_stories);
        refreshLayout = view.findViewById(R.id.refresh_stories);
        noNetRefresh = view.findViewById(R.id.refresh_stories_button);
        noStories = view.findViewById(R.id.no_stories);
        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StoriesListAdapter(getActivity(), true);
//        adapter.setUserObjects(userObjectListCopy, userObjectList);
        recyclerView.setAdapter(adapter);
        adContainer = view.findViewById(R.id.adContainer);
        showBannerAd();
        loadStories();
        loadNativeAds();


        noNetRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadStories();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!userObjectList.isEmpty()) {
                    userObjectList.clear();
                    userObjectListCopy.clear();
                    userObjectListForSearching.clear();
                }

                loadStories();
            }
        });



        return view;
    }


    private void loadStories() {
        if (ZoomstaUtil.haveNetworkConnection(getActivity())) {
            new GetStoriesFeed().execute(new Boolean[]{Boolean.FALSE});
        } else {
            noNet.setVisibility(View.VISIBLE);
            if (refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_icon, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putParcelableArrayListExtra("userObjectList", (ArrayList<? extends Parcelable>) userObjectListCopy);
                startActivity(intent);

                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
    }

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
        } catch (Exception e) {
            Log.e(TAG, "showBannerAd: " + e);
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
