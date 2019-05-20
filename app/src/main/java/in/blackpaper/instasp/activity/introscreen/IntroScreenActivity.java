package in.blackpaper.instasp.activity.introscreen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import androidx.viewpager.widget.ViewPager;
import butterknife.ButterKnife;
import in.blackpaper.instasp.BuildConfig;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.MainActivity;
import in.blackpaper.instasp.adapter.IntroScreenAdapter;
import in.blackpaper.instasp.base.BaseActivity;
import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.data.localpojo.IntroScreens;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.Utility;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.RegularButton;
import in.blackpaper.instasp.view.RegularEditText;

public class IntroScreenActivity extends BaseActivity<IntroScreenPresenter> implements IntroScreenContractor.View {
    ViewPager introSlider;
    IntroScreenAdapter introPagerAdapter;
    WormDotsIndicator dotsIndicator;
    LinearLayout instaLogin;
    Context mContext;

    private static final String TAG = IntroScreenActivity.class.getSimpleName();

    @Override
    public IntroScreenPresenter createPresenter() {
        return new IntroScreenPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mContext = this;
        setContentView(R.layout.activity_intro_screen);
        initUI();
        onClick();


    }


    private void initUI() {
        introSlider = findViewById(R.id.pager);
        dotsIndicator = (WormDotsIndicator) findViewById(R.id.dots_indicator);
        Utility.setActionBarColor(IntroScreenActivity.this);
        IntroScreens.getScreen().clear();
        introPagerAdapter = new IntroScreenAdapter(this, IntroScreens.getScreen());
        introSlider.setAdapter(introPagerAdapter);
        dotsIndicator.setViewPager(introSlider);
        instaLogin = findViewById(R.id.instaLogin);


    }

    public void onClick() {
        instaLogin.setOnClickListener(v -> {
            showInstaLoginDialog();
        });

    }

    private class Sign extends AsyncTask<String, String, String> {
        String resp;
        String username,password;

        private Sign(String username,String password) {
            this.username = username;
            this.password = password;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        protected String doInBackground(String... args) {
            try {
                this.resp = InstaUtils.login(username,password);
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String img) {
            try {
                if (this.resp.equals(BuildConfig.VERSION_NAME)) {
                    hideLoading();
                    ToastUtils.ErrorToast(mContext, "User not found");
                }
                if (this.resp.equals("true")) {
                    ZoomstaUtil.setStringPreference(mContext, InstaUtils.getCookies(), "cooki");
                    ZoomstaUtil.setStringPreference(mContext, InstaUtils.getCsrf(), "csrf");
                    ZoomstaUtil.setStringPreference(mContext, InstaUtils.getUserId(), "userid");
                    ZoomstaUtil.setStringPreference(mContext, InstaUtils.getSessionid(), "sessionid");
                    ZoomstaUtil.setStringPreference(mContext,username , "username");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(IntroScreenActivity.this, MainActivity.class);
                            i.putExtra("user", InstaUtils.getUserId());

                            hideLoading();
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            IntroScreenActivity.this.finish();
                            IntroScreenActivity.this.startActivity(i);
                            IntroScreenActivity.this.overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                        }
                    }, 1500);
                } else if (this.resp.equals("false")) {
                    hideLoading();
                    ToastUtils.ErrorToast(IntroScreenActivity.this, "Incorrect Username / Password");
                } else {

                    hideLoading();
                    ToastUtils.ErrorToast(IntroScreenActivity.this, "Problem occurred logging in. Please try again");
                }
            } catch (Exception e) {

                hideLoading();
                ToastUtils.ErrorToast(IntroScreenActivity.this, "Problem occurred logging in. Please try again");
            }
        }
    }

    public void showInstaLoginDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.item_auth_dialog, null);
        android.app.AlertDialog.Builder alertDialogBuilderUserInput = new android.app.AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);
        final android.app.AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        alertDialogAndroid.setCancelable(false);

        final RegularButton goBack, loginInstagram;
        RegularEditText username, password;

        goBack = alertDialogAndroid.findViewById(R.id.goBack);
        loginInstagram = alertDialogAndroid.findViewById(R.id.loginInstagram);
        username = alertDialogAndroid.findViewById(R.id.username);
        password = alertDialogAndroid.findViewById(R.id.password);


        goBack.setOnClickListener(v -> {
            alertDialogAndroid.dismiss();
        });

        loginInstagram.setOnClickListener(v -> {
            if (TextUtils.isEmpty(username.getText().toString()))
                ToastUtils.ErrorToast(mContext, mContext.getString(R.string.username_cant_empty));
            else if (TextUtils.isEmpty(password.getText().toString()))
                ToastUtils.ErrorToast(mContext, mContext.getString(R.string.password_cant_empty));
            else {
                new Sign(username.getText().toString(),password.getText().toString()).execute();
            }
            alertDialogAndroid.dismiss();

        });
    }


    @Override
    public void updateViewForInstagramLogin(InstagramLoginResponse instagramLoginResponse) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
