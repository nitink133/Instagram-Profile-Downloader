package in.blackpaper.instasp.contractor;

import androidx.lifecycle.LiveData;

import java.util.List;

import in.blackpaper.instasp.base.mvp.MvpView;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import io.reactivex.Observable;

public interface IntroScreenContractor {
    public interface Presenter {
        void loginInstagram(String username, String password);

        long addNewUser(Logins logins);
    }

    public interface View extends MvpView {

        void updateViewForInstagramLogin(InstagramLoginResponse instagramLoginResponse);

    }

    public interface Interactor {


        Observable<InstagramLoginResponse> loginInstagram(String username, String password);


        long addNewUser(Logins logins);


    }
}
