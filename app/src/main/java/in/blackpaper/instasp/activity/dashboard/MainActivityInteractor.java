package in.blackpaper.instasp.activity.dashboard;

import androidx.lifecycle.LiveData;

import java.util.List;

import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.contractor.MainActivityContractor;
import in.blackpaper.instasp.contractor.SplashActivityContractor;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.retrofit.response.IntagramProfileResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import io.reactivex.Observable;

public class MainActivityInteractor implements MainActivityContractor.Interactor {
    DataObjectRepositry dataRepository;


    public MainActivityInteractor() {
        dataRepository = DataObjectRepositry.dataObjectRepositry;
    }


    @Override
    public LiveData<List<Logins>> getAllLoggedInUsers() {
        return dataRepository.getAllUsers();
    }

    @Override
    public Observable<IntagramProfileResponse> getUserProfileData(String url) {
        return dataRepository.getUserProfileData(url);
    }
}
