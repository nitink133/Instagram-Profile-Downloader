package in.blackpaper.instasp.base;

import java.util.HashMap;

import in.blackpaper.instasp.base.mvp.MvpPresenter;


public class PresenterManager {
    private final static HashMap<String, MvpPresenter> presenterPool = new HashMap<>();

    public static void putPresenter(String activityId, MvpPresenter presenter){
        presenterPool.put(activityId,presenter);
    }

    public static <P> P getPresenter(String activityId){
        if (activityId == null) {
            throw new NullPointerException("View id is null");
        }

        P presenter = (P)presenterPool.get(activityId);
        return presenter == null ? null : presenter;
    }

    public static void remove(String activityId){
        if (activityId == null) {
            throw new NullPointerException("View id is null");
        }
        presenterPool.remove(activityId);
    }
}
