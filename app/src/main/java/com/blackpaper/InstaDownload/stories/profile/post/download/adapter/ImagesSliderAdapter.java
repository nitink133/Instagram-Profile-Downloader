package com.blackpaper.InstaDownload.stories.profile.post.download.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.blackpaper.InstaDownload.stories.profile.post.download.view.RegularTextView;

public class ImagesSliderAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<IntagramProfileResponse.ThumbnailResource> screenLists;

    public ImagesSliderAdapter(Context context, List<IntagramProfileResponse.ThumbnailResource> items) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        screenLists = items;

    }

    @Override
    public int getCount() {
        return screenLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_row_screen, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        RegularTextView title = itemView.findViewById(R.id.tv_title);
        RegularTextView subTitle = itemView.findViewById(R.id.tv_info);
        Glide.with(mContext).load(screenLists.get(position).getSrc()).into(imageView);
        container.addView(itemView);
        return itemView;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}



