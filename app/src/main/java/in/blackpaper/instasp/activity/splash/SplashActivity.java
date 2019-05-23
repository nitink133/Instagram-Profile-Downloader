package in.blackpaper.instasp.activity.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.dashboard.MainActivity;
import in.blackpaper.instasp.activity.introscreen.IntroScreenActivity;
import in.blackpaper.instasp.base.BaseActivity;
import in.blackpaper.instasp.contractor.SplashActivityContractor;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.data.room.tables.Logins;

public class SplashActivity extends BaseActivity<SplashActivityPresenter> implements SplashActivityContractor.View {

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

        String token = PreferencesManager.getPref(GlobalConstant.TOKEN);
        if (token != null && !token.isEmpty()) {

            LiveData<List<Logins>> listLiveData = mPresenter.getAllLoggedInUsers();

            listLiveData.observe(this, new Observer<List<Logins>>() {
                @Override
                public void onChanged(@Nullable List<Logins> sections) {
                    if (listLiveData != null && listLiveData.getValue() != null && listLiveData.getValue().size() > 0) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, IntroScreenActivity.class));
                    }

                }
            });
        } else
            startActivity(new Intent(SplashActivity.this, IntroScreenActivity.class));


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
