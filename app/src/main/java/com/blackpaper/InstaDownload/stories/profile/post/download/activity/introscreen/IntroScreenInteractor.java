package com.blackpaper.InstaDownload.stories.profile.post.download.activity.introscreen;

import com.blackpaper.InstaDownload.stories.profile.post.download.GlobalConstant;
import com.blackpaper.InstaDownload.stories.profile.post.download.contractor.IntroScreenContractor;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.repositry.DataObjectRepositry;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.retrofit.response.InstagramLoginResponse;
import com.blackpaper.InstaDownload.stories.profile.post.download.data.room.tables.Logins;
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
