package com.storyPost.PhotoVideoDownloader.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.ViewStoryActivity;
import com.storyPost.PhotoVideoDownloader.models.StoriesWithNativeAd;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;
import com.storyPost.PhotoVideoDownloader.utils.SquareLayout;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;
import com.storyPost.PhotoVideoDownloader.view.StoriesNativeAdView;
import com.storyPost.PhotoVideoDownloader.viewholder.UnifiedNativeAdViewHolder;

/**
 * Created by nitin on 25/5/19.
 */

public class StoriesOverViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> modelList=new ArrayList<>();
    private List<String> storyModels= new ArrayList<>();
    private Context context;
    private int spaceBetweenAds;

    public StoriesOverViewAdapter(Context context, int spaceBetweenAds) {
        this.context = context;
        this.spaceBetweenAds = spaceBetweenAds;
    }


    public void addAll(List<Object> m,List<String> s){
        modelList.clear();
        storyModels.clear();
        modelList.addAll(m);
        storyModels.addAll(s);
        notifyDataSetChanged();
    }


    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }


        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case GlobalConstant.CONTENT_TYPE:
                View dataLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.stories_overview_object, viewGroup, false);
                return new StoriesOverViewHolder(dataLayoutView);
            case GlobalConstant.AD_TYPE:
                // fall through
            default:
                View nativeExpressLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.item_native_ad,
                        viewGroup, false);
                return new UnifiedNativeAdViewHolder(nativeExpressLayoutView);
        }





    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position) {


        int viewType = getItemViewType(position);

        // Binding data based on View Type
        switch (viewType) {
            case GlobalConstant.CONTENT_TYPE:
                StoriesOverViewHolder holder = (StoriesOverViewHolder) holder1;

                holder.setIsRecyclable(false);
                final String model = (String) modelList.get(position);

                if (!model.endsWith(".jpg")) {
                    holder.isVideo.setVisibility(View.VISIBLE);
                    Glide.with(context).load(model).thumbnail(0.2f).into(holder.imageView);
                } else {
                    holder.isVideo.setVisibility(View.GONE);
                    String url = model.substring(0, model.length() - 4);

                    Glide.with(context).load(url).thumbnail(0.2f).into(holder.imageView);
                }

                holder.layout.setVisibility(View.VISIBLE);

                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                Intent intent = new Intent(context, ViewStoryActivity.class);
//                DataHolder.setData(storyModels);
//                intent.putExtra("isLarge", true);
//                intent.putExtra("pos", position);
//                context.startActivity(intent);



                        Intent intent = new Intent(context, ViewStoryActivity.class);
                        intent.putExtra("isFromNet", true);
                        intent.putStringArrayListExtra("urls", (ArrayList<String>) storyModels);
                        intent.putExtra("pos", position);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                    }
                });

                break;
            case GlobalConstant.AD_TYPE:
                // fall through
            default:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) modelList.get(position);
                populateNativeAdView(nativeAd, ((StoriesNativeAdView) holder1).getAdView());

        }


    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }


    public class StoriesOverViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView isVideo;
        private SquareLayout layout;

        public StoriesOverViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.overview_media_holder);
            isVideo = itemView.findViewById(R.id.overview_is_video);
            this.layout = itemView.findViewById(R.id.select_stories_overview_item);
        }
    }



    @Override
    public int getItemViewType(int position) {
        // Logic for returning view type based on spaceBetweenAds variable
        // Here if remainder after dividing the position with (spaceBetweenAds + 1) comes equal to spaceBetweenAds,
        // then return NATIVE_EXPRESS_AD_VIEW_TYPE otherwise DATA_VIEW_TYPE
        // By the logic defined below, an ad unit will be showed after every spaceBetweenAds numbers of data items
        return modelList.get(position) instanceof String?GlobalConstant.CONTENT_TYPE:GlobalConstant.AD_TYPE;
    }


}
