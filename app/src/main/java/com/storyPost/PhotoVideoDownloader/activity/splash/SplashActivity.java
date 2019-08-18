package com.storyPost.PhotoVideoDownloader.activity.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.dashboard.MainActivity;
import com.storyPost.PhotoVideoDownloader.activity.introscreen.IntroScreenActivity;
import com.storyPost.PhotoVideoDownloader.base.BaseActivity;
import com.storyPost.PhotoVideoDownloader.contractor.SplashActivityContractor;
import com.storyPost.PhotoVideoDownloader.data.prefs.PreferencesManager;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

public class SplashActivity extends BaseActivity<SplashActivityPresenter> implements SplashActivityContractor.View {
    boolean isLogOut;

    @Override
    public SplashActivityPresenter createPresenter() {
        return new SplashActivityPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if(getIntent()!=null){
            isLogOut = getIntent().getBooleanExtra("isLogOut",false);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                        LiveData<List<Logins>> listLiveData = mPresenter.getAllLoggedInUsers();

                        listLiveData.observe(SplashActivity.this, new Observer<List<Logins>>() {
                            @Override
                            public void onChanged(@Nullable List<Logins> sections) {
                                if (sections != null && sections.size() > 0) {
                                    InstaUtils.setSessionId(sections.get(0).getSession_id());
                                    InstaUtils.setUserId(sections.get(0).getUserId());
                                    InstaUtils.setCookies(sections.get(0).getCooki());
                                    InstaUtils.setCsrf(sections.get(0).getCsrf(), sections.get(0).getCooki());

                                    PreferencesManager.savePref(GlobalConstant.USERNAME, sections.get(0).getUserName());
                                    PreferencesManager.savePref(GlobalConstant.USER_ID, InstaUtils.getUserId());
                                    PreferencesManager.savePref(GlobalConstant.TOKEN, InstaUtils.getSessionid());
                                    PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, sections.get(0).getProfilePic());


                                    ZoomstaUtil.setStringPreference(SplashActivity.this, InstaUtils.getCookies(), "cooki");
                                    ZoomstaUtil.setStringPreference(SplashActivity.this, InstaUtils.getCsrf(), "csrf");
                                    ZoomstaUtil.setStringPreference(SplashActivity.this, InstaUtils.getSessionid(), "sessionid");
                                    ZoomstaUtil.setStringPreference(SplashActivity.this, InstaUtils.getUserId(), "userid");
                                    ZoomstaUtil.setStringPreference(SplashActivity.this, sections.get(0).getUserId(), "username");

                                    PreferencesManager.savePref(GlobalConstant.FULL_NAME, sections.get(0).getFullName());
                                    PreferencesManager.savePref(GlobalConstant.BIO, sections.get(0).getBio());
                                    PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY, sections.get(0).getFollowedBy());
                                    PreferencesManager.savePref(GlobalConstant.FOLLOWS, sections.get(0).getFollows());
                                    PreferencesManager.savePref(GlobalConstant.MEDIA, sections.get(0).getMedia());


                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(SplashActivity.this, IntroScreenActivity.class));
                                }

                            }
                        });


            }
        },1500);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
