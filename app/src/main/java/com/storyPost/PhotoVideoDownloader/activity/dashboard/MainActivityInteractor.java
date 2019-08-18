package com.storyPost.PhotoVideoDownloader.activity.dashboard;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.contractor.MainActivityContractor;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.retrofit.response.IntagramProfileResponse;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
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


}
