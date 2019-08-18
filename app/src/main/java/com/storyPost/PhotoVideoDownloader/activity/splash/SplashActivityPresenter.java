package com.storyPost.PhotoVideoDownloader.activity.splash;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.base.BasePresenter;
import com.storyPost.PhotoVideoDownloader.contractor.SplashActivityContractor;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;

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