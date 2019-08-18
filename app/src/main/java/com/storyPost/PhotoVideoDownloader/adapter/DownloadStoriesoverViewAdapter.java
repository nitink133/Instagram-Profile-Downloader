package com.storyPost.PhotoVideoDownloader.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.NativeExpressAdView;
import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.ViewStoryActivity;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;
import com.storyPost.PhotoVideoDownloader.utils.SquareLayout;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;
import com.storyPost.PhotoVideoDownloader.viewholder.UnifiedNativeAdViewHolder;

public class DownloadStoriesoverViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Object> modelList = new ArrayList<>();
    public List<String> modelListCopy = new ArrayList<>();
    public List<StoryModel> storyModels = new ArrayList<>();
    private Context context;
    private int spaceBetweenAds;

    public ArrayList<StoryModel> selected_usersList = new ArrayList<>();
    private int count;

    public DownloadStoriesoverViewAdapter(Context context, int spaceBetweenAds) {
        this.context = context;
        this.spaceBetweenAds = spaceBetweenAds;
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

    public void addAll(ArrayList<Object> modelList, ArrayList<StoryModel> stories, ArrayList<String> modeListCopy) {
        this.modelList.clear();
        this.storyModels.clear();
        this.modelListCopy.clear();
        this.modelList.addAll(modelList);
        this.modelListCopy.addAll(modeListCopy);
        this.storyModels.addAll(stories);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, final int position) {


        int viewType = getItemViewType(position);

        // Binding data based on View Type
        switch (viewType) {
            case GlobalConstant.CONTENT_TYPE:
                StoriesOverViewHolder holder = (StoriesOverViewHolder) holder1;


                holder.setIsRecyclable(false);
                final String model = modelListCopy.get(position);

                Glide.with(context).load(model).thumbnail(0.2f).into(holder.imageView);
                holder.layout.setVisibility(View.VISIBLE);
                if (!model.endsWith(".jpg"))
                    holder.isVideo.setVisibility(View.VISIBLE);
                else holder.isVideo.setVisibility(View.GONE);


                for (StoryModel storyModel : selected_usersList) {
                    if (storyModel.getFilePath().equalsIgnoreCase(model)) {
                        holder.checked.setVisibility(View.VISIBLE);
                        break;
                    } else {

                        holder.checked.setVisibility(View.GONE);
                    }
                }
                holder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                Intent intent = new Intent(context, ViewStoryActivity.class);
//                DataHolder.setData(storyModels);
//                intent.putExtra("isLarge", true);
//                intent.putExtra("pos", position);
//                context.startActivity(intent);
                        count = ZoomstaUtil.getIntegerPreference(context, "itemCount");


                        Intent intent = new Intent(context, ViewStoryActivity.class);
                        intent.putExtra("isFromNet", false);
                        intent.putParcelableArrayListExtra("storylist", (ArrayList<StoryModel>) storyModels);
                        intent.putExtra("pos", position);
                        intent.putExtra("isFromDownloadScreen", true);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                    }
                });


                break;
            case GlobalConstant.AD_TYPE:
                // fall through
            default:
                UnifiedNativeAdViewHolder nativeExpressHolder = (UnifiedNativeAdViewHolder) holder1;
                NativeExpressAdView adView = (NativeExpressAdView) modelList.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;

                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                adCardView.addView(adView);
        }


    }

    @Override
    public int getItemCount() {
        return storyModels.size();
    }


    public class StoriesOverViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView isVideo;
        private SquareLayout layout;
        private ImageButton checked;

        public StoriesOverViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.overview_media_holder);
            isVideo = itemView.findViewById(R.id.overview_is_video);
            this.layout = itemView.findViewById(R.id.select_stories_overview_item);
            this.checked = itemView.findViewById(R.id.checked);
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
