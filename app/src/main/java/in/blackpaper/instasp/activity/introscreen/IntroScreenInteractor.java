package in.blackpaper.instasp.activity.introscreen;

import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import io.reactivex.Observable;

public class IntroScreenInteractor implements IntroScreenContractor.Interactor {
    DataObjectRepositry dataRepository;


    public IntroScreenInteractor() {
        dataRepository = new DataObjectRepositry();
    }

    @Override
    public Observable<InstagramLoginResponse> loginInstagram(String username, String password) {
        return dataRepository.loginInstagram(username,password);
    }


}
