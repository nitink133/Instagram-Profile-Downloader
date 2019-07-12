package com.storyPost.PhotoVideoDownloader.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;


import java.util.List;

import com.storyPost.PhotoVideoDownloader.fragments.StoryFragment;
import com.storyPost.PhotoVideoDownloader.models.StoryModel;

/**
 * Created by tirgei on 11/4/17.
 */

public class StoryViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<StoryModel> modelList;
    private List<String> list;
    private Boolean isFromNet;
    private Boolean isDownloadPostImage,isAlreadyDownloaded,isFromDownloadScreen;

    public StoryViewPagerAdapter(FragmentManager fm, List<StoryModel> models, Boolean isFromNet, List<String> list,Boolean isDownloadPostImage,
                                 boolean isAlreadyDownloaded,boolean isFromDownloadScreen){
        super(fm);

        this.modelList = models;
        this.isFromNet = isFromNet;
        this.list = list;
        this.isDownloadPostImage = isDownloadPostImage;
        this.isAlreadyDownloaded=isAlreadyDownloaded;
        this.isFromDownloadScreen =isFromDownloadScreen;

    }

    @Override
    public Fragment getItem(int position) {
        StoryFragment storyFragment;

        if(isFromNet){
            storyFragment = StoryFragment.newInstance(true);
            String url = list.get(position);
            storyFragment.getStories(url);
            storyFragment.setIsDownloadImagePost(isDownloadPostImage);
            storyFragment.setDownloadedFile(isFromDownloadScreen);

        } else {
            storyFragment = StoryFragment.newInstance(false);
            StoryModel story = modelList.get(position);

            storyFragment.setAlreadyDownloaded(isAlreadyDownloaded);
            storyFragment.setDownloadedFile(isFromDownloadScreen);
            storyFragment.setStoryList(story);
        }

        return storyFragment;
    }

    @Override
    public int getCount() {
        if(!isFromNet)
            return modelList.size();
        else
            return list.size();
    }

    public void deletePage(int position) {
        if (canDelete()) {
            modelList.remove(position);
            notifyDataSetChanged();
        }
    }

    boolean canDelete() {
        return modelList.size() > 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


}
