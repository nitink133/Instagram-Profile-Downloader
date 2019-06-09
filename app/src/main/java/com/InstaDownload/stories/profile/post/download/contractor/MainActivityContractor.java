package com.InstaDownload.stories.profile.post.download.contractor;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.InstaDownload.stories.profile.post.download.base.mvp.MvpView;
import com.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;
import io.reactivex.Observable;

public interface MainActivityContractor {
    public interface Presenter {

        LiveData<List<Logins>> getAllLoggedInUsers();

        void getUserProfileData(String url);
    }

    public interface View extends MvpView {

    }

    public interface Interactor {


        LiveData<List<Logins>> getAllLoggedInUsers();

        Observable<IntagramProfileResponse> getUserProfileData(String url);


    }
}
