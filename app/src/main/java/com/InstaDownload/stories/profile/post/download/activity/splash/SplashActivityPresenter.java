package com.InstaDownload.stories.profile.post.download.activity.splash;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.InstaDownload.stories.profile.post.download.base.BasePresenter;
import com.InstaDownload.stories.profile.post.download.contractor.SplashActivityContractor;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;

public class SplashActivityPresenter  extends BasePresenter<SplashActivityContractor.View> implements SplashActivityContractor.Presenter {

    SplashActivityInteractor splashActivityInteractor;

    public SplashActivityPresenter() {
        splashActivityInteractor = new SplashActivityInteractor();
    }



    @Override
    public LiveData<List<Logins>> getAllLoggedInUsers() {
        return splashActivityInteractor.getAllLoggedInUsers();
    }
}