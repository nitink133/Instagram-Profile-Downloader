package in.blackpaper.instasp.data.localpojo;

import java.util.ArrayList;

import in.blackpaper.instasp.R;

public class IntroScreens {
    static ArrayList<IntroScreenList> introScreenLists = new ArrayList<>();
    static IntroScreenList screenList;

    public static ArrayList<IntroScreenList> getScreen() {
        screenList = new IntroScreenList("Save what you want,\nwhen you want", R.drawable.ic_download_posts, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.");
        introScreenLists.add(screenList);
        screenList = new IntroScreenList("Focus your study around the Gita", R.drawable.ic_selfie, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.");
        introScreenLists.add(screenList);
        screenList = new IntroScreenList("Lost your phone again ?", R.drawable.ic_launcher_background, "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.");
        introScreenLists.add(screenList);
        return introScreenLists;
    }
}
