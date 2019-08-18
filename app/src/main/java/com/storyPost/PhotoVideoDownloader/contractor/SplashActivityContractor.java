package com.storyPost.PhotoVideoDownloader.contractor;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.base.mvp.MvpView;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;

public interface SplashActivityContractor {
    public interface Presenter {

        LiveData<List<Logins>> getAllLoggedInUsers();
    }

    public interface View extends MvpView {


    }

    public interface Interactor {


        LiveData<List<Logins>> getAllLoggedInUsers();


    }
}
