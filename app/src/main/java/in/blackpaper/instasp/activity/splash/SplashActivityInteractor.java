package in.blackpaper.instasp.activity.splash;

import androidx.lifecycle.LiveData;

import java.util.List;

import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.contractor.SplashActivityContractor;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import io.reactivex.Observable;

public class SplashActivityInteractor implements SplashActivityContractor.Interactor {
    DataObjectRepositry dataRepository;


    public SplashActivityInteractor() {
        dataRepository = DataObjectRepositry.dataObjectRepositry;
    }


    @Override
    public LiveData<List<Logins>> getAllLoggedInUsers() {
        return dataRepository.getAllUsers();
    }
}
