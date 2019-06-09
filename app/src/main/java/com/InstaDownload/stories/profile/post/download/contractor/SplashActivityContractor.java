package com.InstaDownload.stories.profile.post.download.contractor;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.InstaDownload.stories.profile.post.download.base.mvp.MvpView;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;

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
