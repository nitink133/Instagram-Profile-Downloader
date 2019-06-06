package com.blackpaper.InstaDownload.stories.profile.post.download.utils;

import android.util.Log;

import java.util.List;

import com.blackpaper.InstaDownload.stories.profile.post.download.models.StoryModel;

/**
 * Created by tirgei on 11/4/17.
 */

public enum DataHolder {
    INSTANCE;

    private List<StoryModel> mObjectList;

    public static void setData(final List<StoryModel> objectList) {
        INSTANCE.mObjectList = objectList;
        Log.d("data", "Data is set" + objectList.size());
    }

    public static List<StoryModel> getData() {
        final List<StoryModel> retList = INSTANCE.mObjectList;
        INSTANCE.mObjectList = null;
        Log.d("data", "Data is fetched");
        return retList;
    }
}