package com.storyPost.PhotoVideoDownloader.activity.splash;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.contractor.SplashActivityContractor;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;

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
