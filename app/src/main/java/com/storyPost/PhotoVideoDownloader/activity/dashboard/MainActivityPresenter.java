package com.storyPost.PhotoVideoDownloader.activity.dashboard;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.base.BasePresenter;
import com.storyPost.PhotoVideoDownloader.contractor.MainActivityContractor;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivityPresenter extends BasePresenter<MainActivityContractor.View> implements MainActivityContractor.Presenter {

    MainActivityInteractor mainActivityInteractor;

    public MainActivityPresenter() {
        mainActivityInteractor = new MainActivityInteractor();
    }


    @Override
    public LiveData<List<Logins>> getAllLoggedInUsers() {
        return mainActivityInteractor.getAllLoggedInUsers();
    }


}