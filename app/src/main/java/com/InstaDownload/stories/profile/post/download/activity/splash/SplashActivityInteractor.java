package com.InstaDownload.stories.profile.post.download.activity.splash;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.InstaDownload.stories.profile.post.download.contractor.SplashActivityContractor;
import com.InstaDownload.stories.profile.post.download.data.repositry.DataObjectRepositry;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;

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
