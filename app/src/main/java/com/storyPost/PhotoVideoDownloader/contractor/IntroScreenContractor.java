package com.storyPost.PhotoVideoDownloader.contractor;

import com.storyPost.PhotoVideoDownloader.base.mvp.MvpView;
import com.storyPost.PhotoVideoDownloader.data.retrofit.response.InstagramLoginResponse;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
import io.reactivex.Observable;

public interface IntroScreenContractor {
    public interface Presenter {
        long addNewUser(Logins logins);
    }

    public interface View extends MvpView {


    }

    public interface Interactor {




        long addNewUser(Logins logins);


    }
}
