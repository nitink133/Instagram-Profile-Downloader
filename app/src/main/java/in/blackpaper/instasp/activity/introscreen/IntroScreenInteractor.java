package in.blackpaper.instasp.activity.introscreen;

import androidx.lifecycle.LiveData;

import java.util.List;

import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import io.reactivex.Observable;

public class IntroScreenInteractor implements IntroScreenContractor.Interactor {
    DataObjectRepositry dataRepository;


    public IntroScreenInteractor() {
        dataRepository = DataObjectRepositry.dataObjectRepositry;
    }

    @Override
    public Observable<InstagramLoginResponse> loginInstagram(String username, String password) {
        return dataRepository.loginInstagram(GlobalConstant.BASE_URL,username,password);
    }

    @Override
    public long addNewUser(Logins logins) {
    return dataRepository.addNewUser(logins);
    }



}
