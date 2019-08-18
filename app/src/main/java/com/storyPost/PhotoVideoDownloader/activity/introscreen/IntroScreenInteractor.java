package com.storyPost.PhotoVideoDownloader.activity.introscreen;

import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.contractor.IntroScreenContractor;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.retrofit.response.InstagramLoginResponse;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
import io.reactivex.Observable;

public class IntroScreenInteractor implements IntroScreenContractor.Interactor {
    DataObjectRepositry dataRepository;


    public IntroScreenInteractor() {
        dataRepository = DataObjectRepositry.dataObjectRepositry;
    }



    @Override
    public long addNewUser(Logins logins) {
    return dataRepository.addNewUser(logins);
    }



}
