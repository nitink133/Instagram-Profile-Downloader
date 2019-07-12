package com.storyPost.PhotoVideoDownloader.data.localpojo;

import java.util.ArrayList;

import com.storyPost.PhotoVideoDownloader.R;

public class IntroScreens {
    static ArrayList<IntroScreenList> introScreenLists = new ArrayList<>();
    static IntroScreenList screenList;

    public static ArrayList<IntroScreenList> getScreen() {
        screenList = new IntroScreenList("Authenticate with official Instagram", R.mipmap.instadownloader_icon, "We respect your privacy,we are not storing your password.");
        introScreenLists.add(screenList);
        screenList = new IntroScreenList("Download your favourite posts", R.drawable.ic_download_posts, "InstaDownload let you download Instagram stories,profile picture and many more.");
        introScreenLists.add(screenList);
        screenList = new IntroScreenList("Stories downloader", R.drawable.ic_selfie, "Download stories of your loved ones");
        introScreenLists.add(screenList);
        return introScreenLists;
    }
}
