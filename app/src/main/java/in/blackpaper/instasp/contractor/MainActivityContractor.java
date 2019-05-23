package in.blackpaper.instasp.contractor;

import androidx.lifecycle.LiveData;

import java.util.List;

import in.blackpaper.instasp.base.mvp.MvpView;
import in.blackpaper.instasp.data.retrofit.response.IntagramProfileResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
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
