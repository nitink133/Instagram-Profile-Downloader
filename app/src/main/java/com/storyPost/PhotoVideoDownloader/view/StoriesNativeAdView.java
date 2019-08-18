package com.storyPost.PhotoVideoDownloader.view;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.storyPost.PhotoVideoDownloader.R;

public class StoriesNativeAdView extends RecyclerView.ViewHolder {

    private UnifiedNativeAdView adView;

    public UnifiedNativeAdView getAdView() {
        return adView;
    }

   public  StoriesNativeAdView(View view) {
        super(view);
        adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);


        // Register the view used for each individual asset.
        adView.setHeadlineView(adView.findViewById(R.id.body));
        adView.setCallToActionView(adView.findViewById(R.id.viewAdd));
        adView.setIconView(adView.findViewById(R.id.image));
    }
}