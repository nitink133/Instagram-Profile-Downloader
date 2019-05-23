package in.blackpaper.instasp.activity.splash;

import androidx.lifecycle.LiveData;

import java.util.List;

import in.blackpaper.instasp.activity.introscreen.IntroScreenInteractor;
import in.blackpaper.instasp.base.BasePresenter;
import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.contractor.SplashActivityContractor;
import in.blackpaper.instasp.data.room.tables.Logins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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