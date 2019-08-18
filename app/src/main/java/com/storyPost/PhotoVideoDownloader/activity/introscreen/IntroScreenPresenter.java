package com.storyPost.PhotoVideoDownloader.activity.introscreen;

import com.storyPost.PhotoVideoDownloader.base.BasePresenter;
import com.storyPost.PhotoVideoDownloader.contractor.IntroScreenContractor;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class IntroScreenPresenter extends BasePresenter<IntroScreenContractor.View> implements IntroScreenContractor.Presenter {

    IntroScreenInteractor introScreenInteractor;

    public IntroScreenPresenter() {
        introScreenInteractor = new IntroScreenInteractor();
    }

    @Override
    public long addNewUser(Logins logins) {

        return introScreenInteractor.addNewUser(logins);
    }


}




