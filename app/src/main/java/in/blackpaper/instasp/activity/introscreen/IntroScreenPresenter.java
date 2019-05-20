package in.blackpaper.instasp.activity.introscreen;

import in.blackpaper.instasp.base.BasePresenter;
import in.blackpaper.instasp.contractor.IntroScreenContractor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IntroScreenPresenter extends BasePresenter<IntroScreenContractor.View> implements IntroScreenContractor.Presenter {

    IntroScreenInteractor introScreenInteractor;

    public IntroScreenPresenter() {
        introScreenInteractor = new IntroScreenInteractor();
    }

    @Override
    public void loginInstagram(String username, String password) {

        getCompositeDisposable().add(introScreenInteractor.loginInstagram(username,password)
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



}




