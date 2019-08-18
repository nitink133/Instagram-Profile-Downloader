package com.storyPost.PhotoVideoDownloader.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.adapter.StoriesOverViewAdapter;
import com.storyPost.PhotoVideoDownloader.models.StoriesWithNativeAd;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;
import com.storyPost.PhotoVideoDownloader.models.UserObject;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tirgei on 11/4/17.
 */

public class StoriesOverview extends DialogFragment {
    private String TAG = StoriesOverview.class.getName();
    private TextView username;
    private RecyclerView recyclerView;
    private ArrayList<Object> modelList;
    private ArrayList<String> modelListCopy;
    private ArrayList<StoryModel> stories;
    private StoriesOverViewAdapter adapter;
    private UserObject user;
    private String name, id;
    private LinearLayout noNet, noStories;
    private SpinKitView wave;
    private ImageView back;
    private AdView adView;
    private com.facebook.ads.AdView adFbView;
    int spaceBetweenAds = 7;
    LinearLayout adContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stories_overview, container, false);
        adContainer = view.findViewById(R.id.adContainer);
        showBannerAd();

        username = view.findViewById(R.id.stories_overview_username);
        recyclerView = view.findViewById(R.id.stories_overview_rv);
        noNet = view.findViewById(R.id.no_net_overview_stories);
        wave = view.findViewById(R.id.loading_stories_overview);
        noStories = view.findViewById(R.id.no_stories_found);
        back = view.findViewById(R.id.back);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelList = new ArrayList<>();
        modelListCopy = new ArrayList<>();
        stories = new ArrayList<>();
        adapter = new StoriesOverViewAdapter(getActivity(), spaceBetweenAds);
        recyclerView.setAdapter(adapter);

        name = getArguments().getString("username");
        id = getArguments().getString("user_id");
        username.setText(name);

        loadNativeAds();
        int count = ZoomstaUtil.getIntegerPreference(getActivity(), "clickCount");
        count++;
        if (count < 6562)
            ZoomstaUtil.setIntegerPreference(getActivity(), count, "clickCount");
        else
            ZoomstaUtil.setIntegerPreference(getActivity(), 1, "clickCount");
        setStories();

        back.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }

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
        List<UnifiedNativeAd> nativeAdList = new ArrayList<>();
        nativeAdList.addAll(mNativeAds);
        nativeAdList.remove(mNativeAds.size()-1);

        for (UnifiedNativeAd ad : nativeAdList) {
            modelList.add(spaceBetweenAds, ad);
        }
        modelList.add(mNativeAds.get(mNativeAds.size()-1));

        adapter.addAll(modelList, modelListCopy);
    }

    private void setStories() {
        if (ZoomstaUtil.haveNetworkConnection(getActivity())) {
            new GetStoriesFeed().execute(new String[0]);
        } else {
            noNet.setVisibility(View.VISIBLE);
        }

    }

    private class GetStoriesFeed extends AsyncTask<String, String, String> {
        private String response;

        private GetStoriesFeed() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wave.setVisibility(View.VISIBLE);
            if (noNet.isShown())
                noNet.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... args) {
            try {

                modelList.addAll(InstaUtils.stories(id, getActivity()));
                modelListCopy.addAll(InstaUtils.stories(id, getActivity()));
                stories.addAll(InstaUtils.fetchStories(id, getActivity()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            wave.setVisibility(View.GONE);

            if (modelList.size() == 0)
                noStories.setVisibility(View.VISIBLE);
            else {
                adapter.addAll(modelList, modelListCopy);
                adLoader.loadAds(new AdRequest.Builder().build(), modelList.size() / spaceBetweenAds + 1);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            adFbView = new com.facebook.ads.AdView(getActivity(), getString(R.string.facebook_banner), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
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
