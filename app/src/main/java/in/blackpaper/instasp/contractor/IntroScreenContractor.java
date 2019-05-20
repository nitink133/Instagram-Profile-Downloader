package in.blackpaper.instasp.contractor;

import in.blackpaper.instasp.base.mvp.MvpView;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import io.reactivex.Observable;

public interface IntroScreenContractor {
    public interface Presenter {
        void loginInstagram(String username, String password);
    }

    public interface View extends MvpView {

        void updateViewForInstagramLogin(InstagramLoginResponse instagramLoginResponse);
    }

    public interface Interactor {


        Observable<InstagramLoginResponse> loginInstagram(String username, String password);

    }
}
