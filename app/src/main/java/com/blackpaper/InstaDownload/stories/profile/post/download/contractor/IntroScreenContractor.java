package com.blackpaper.InstaDownload.stories.profile.post.download.contractor;

import com.blackpaper.InstaDownload.stories.profile.post.download.base.mvp.MvpView;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response.InstagramLoginResponse;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.room.tables.Logins;
import io.reactivex.Observable;

public interface IntroScreenContractor {
    public interface Presenter {
        void loginInstagram(String username, String password);

        long addNewUser(Logins logins);
    }

    public interface View extends MvpView {

        void updateViewForInstagramLogin(InstagramLoginResponse instagramLoginResponse);

    }

    public interface Interactor {


        Observable<InstagramLoginResponse> loginInstagram(String username, String password);


        long addNewUser(Logins logins);


    }
}
