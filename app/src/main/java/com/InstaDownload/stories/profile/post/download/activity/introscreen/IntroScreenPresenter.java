package com.InstaDownload.stories.profile.post.download.activity.introscreen;

import com.InstaDownload.stories.profile.post.download.base.BasePresenter;
import com.InstaDownload.stories.profile.post.download.contractor.IntroScreenContractor;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IntroScreenPresenter extends BasePresenter<IntroScreenContractor.View> implements IntroScreenContractor.Presenter {

    IntroScreenInteractor introScreenInteractor;

    public IntroScreenPresenter() {
        introScreenInteractor = new IntroScreenInteractor();
    }

    @Override
    public void loginInstagram(String username, String password) {

        getCompositeDisposable().add(introScreenInteractor.loginInstagram(username, password)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(instagramLoginResponse -> {
                    if (instagramLoginResponse != null) {
                        if (instagramLoginResponse.getStatus().equals("ok")) {
                            getMvpView().hideLoading();
                            getMvpView().updateViewForInstagramLogin(instagramLoginResponse);
                        } else {
                            getMvpView().hideLoading();
                            getMvpView().onError(instagramLoginResponse.getStatus());
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    getMvpView().onError(throwable.getLocalizedMessage());
                    getMvpView().hideLoading();
                }));
    }

    @Override
    public long addNewUser(Logins logins) {

        return introScreenInteractor.addNewUser(logins);
    }


}




