package in.blackpaper.instasp;

import androidx.appcompat.app.AppCompatActivity;
import in.blackpaper.instasp.activity.MainActivity;
import in.blackpaper.instasp.activity.introscreen.IntroScreenActivity;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.RegularTextView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {
    private RegularTextView splashTitle;
    private ImageView imageView;
    private static final String font = "Billabong.ttf";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        splashTitle = findViewById(R.id.splash_title);
        imageView = findViewById(R.id.spash_logo);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);
        final String id = UUID.randomUUID().toString();

        final String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());


        final Animation zoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        final Animation zoomout = AnimationUtils.loadAnimation(this, R.anim.zoom_out);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.animate().rotation(100).start();
            }
        }, 300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //imageView.setAnimation(zoomout);
                imageView.animate().rotation(-60).start();
            }
        },1600);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (ZoomstaUtil.getStringPreference(SplashActivity.this, "userid").equals(BuildConfig.VERSION_NAME)) {
                    Intent intent = new Intent(SplashActivity.this, IntroScreenActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                    InstaUtils.setCookies(ZoomstaUtil.getStringPreference(SplashActivity.this, "cookie"));
                    InstaUtils.setCsrf(ZoomstaUtil.getStringPreference(SplashActivity.this, "csrf"), null);
                    InstaUtils.setUserId(ZoomstaUtil.getStringPreference(SplashActivity.this, "userid"));
                    InstaUtils.setSessionId(ZoomstaUtil.getStringPreference(SplashActivity.this, "sessionid"));

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                }
            }
        }, 2500);
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
