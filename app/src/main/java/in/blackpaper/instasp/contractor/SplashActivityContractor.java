package in.blackpaper.instasp.contractor;

import androidx.lifecycle.LiveData;

import java.util.List;

import in.blackpaper.instasp.base.mvp.MvpView;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import io.reactivex.Observable;

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
