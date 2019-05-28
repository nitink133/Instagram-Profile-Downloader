package in.blackpaper.instasp.data.localpojo;

import java.util.ArrayList;

import in.blackpaper.instasp.R;

public class IntroScreens {
    static ArrayList<IntroScreenList> introScreenLists = new ArrayList<>();
    static IntroScreenList screenList;

    public static ArrayList<IntroScreenList> getScreen() {
        screenList = new IntroScreenList("Authenticate with official Instagram", R.drawable.ic_instagram, "We respect your privacy,we are not storing your password.");
        introScreenLists.add(screenList);
        screenList = new IntroScreenList("Download your favourite picture", R.drawable.ic_download_posts, "Instasp let you download Instagram stories,profile picture and many more.");
        introScreenLists.add(screenList);
        screenList = new IntroScreenList("Stories downloader", R.drawable.ic_selfie, "Download stories of your loved ones");
        introScreenLists.add(screenList);
        return introScreenLists;
    }
}
