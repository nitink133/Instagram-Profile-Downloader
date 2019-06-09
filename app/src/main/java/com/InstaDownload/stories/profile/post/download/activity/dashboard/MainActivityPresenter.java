package com.InstaDownload.stories.profile.post.download.activity.dashboard;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.InstaDownload.stories.profile.post.download.base.BasePresenter;
import com.InstaDownload.stories.profile.post.download.contractor.MainActivityContractor;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;

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

    @Override
    public void getUserProfileData(String url) {
        getCompositeDisposable().add(mainActivityInteractor.getUserProfileData(url)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(intagramProfileResponse -> {
                    if (intagramProfileResponse != null) {
                        if (intagramProfileResponse.getGraphql() != null && intagramProfileResponse.getGraphql().getUser() != null) {
                            getMvpView().hideLoading();
//                            getMvpView().updateViewForInstagramProfileResponse(intagramProfileResponse);
                        } else {
                            getMvpView().hideLoading();
//                            ToastUtils.ErrorToast((Context) getMvpView(),"Something went wrong");
                            getMvpView().onError("Something went wrong");
                        }
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    getMvpView().onError(throwable.getLocalizedMessage());
                    getMvpView().hideLoading();
                }));
    }
}