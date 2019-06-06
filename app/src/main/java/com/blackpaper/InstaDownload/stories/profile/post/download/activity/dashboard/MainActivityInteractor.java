package com.blackpaper.InstaDownload.stories.profile.post.download.activity.dashboard;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.blackpaper.InstaDownload.stories.profile.post.download.contractor.MainActivityContractor;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.repositry.DataObjectRepositry;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.room.tables.Logins;
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
