package in.blackpaper.instasp.activity;

import androidx.appcompat.app.AppCompatActivity;

import in.blackpaper.instasp.BuildConfig;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.introscreen.IntroScreenActivity;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.RegularTextView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        if(PreferencesManager.getPref(GlobalConstant.TOKEN)!=null){
            startActivity(new Intent(this,MainActivity.class));
        }


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
