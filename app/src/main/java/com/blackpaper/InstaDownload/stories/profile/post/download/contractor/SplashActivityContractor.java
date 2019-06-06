package com.blackpaper.InstaDownload.stories.profile.post.download.contractor;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.blackpaper.InstaDownload.stories.profile.post.download.base.mvp.MvpView;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.room.tables.Logins;

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
