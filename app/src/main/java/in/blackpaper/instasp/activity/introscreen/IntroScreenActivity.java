package in.blackpaper.instasp.activity.introscreen;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import in.blackpaper.instasp.BuildConfig;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.MainActivity;
import in.blackpaper.instasp.adapter.IntroScreenAdapter;
import in.blackpaper.instasp.base.BaseActivity;
import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.data.localpojo.IntroScreens;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.dialog.AuthenticationDialog;
import in.blackpaper.instasp.listener.AuthenticationListener;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.Utility;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.RegularButton;
import in.blackpaper.instasp.view.RegularEditText;

public class IntroScreenActivity extends BaseActivity<IntroScreenPresenter> implements IntroScreenContractor.View, AuthenticationListener {
    ViewPager introSlider;
    IntroScreenAdapter introPagerAdapter;
    WormDotsIndicator dotsIndicator;
    LinearLayout instaLogin;
    AuthenticationDialog authenticationDialog;
    private String token = null;
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
//            showInstaLoginDialog();
            if (token != null) {
                startActivity(new Intent(this, MainActivity.class));
            } else {


                authenticationDialog = new AuthenticationDialog(this);
                authenticationDialog.show(getSupportFragmentManager().beginTransaction(),AuthenticationDialog.TAG);
//                authenticationDialog.setCancelable(true);
//                authenticationDialog.show();
            }
        });

    }

    @Override
    public void onCodeReceived(String auth_token) {
        if (auth_token == null)
            return;
        PreferencesManager.savePref(GlobalConstant.TOKEN, auth_token);
        token = auth_token;
        getUserInfoByAccessToken(token);
    }


    private void getUserInfoByAccessToken(String token) {
        new RequestInstagramAPI().execute();
    }

    private class RequestInstagramAPI extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getResources().getString(R.string.get_user_info_url) + token);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    if (jsonData.has("id")) {
                        //сохранение данных пользователя
                        PreferencesManager.savePref(GlobalConstant.USER_ID, jsonData.getString("id"));
                        PreferencesManager.savePref(GlobalConstant.USERNAME, jsonData.getString("username"));
                        PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, jsonData.getString("profile_picture"));

                        startActivity(new Intent(IntroScreenActivity.this,MainActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),getString(R.string.some_error),Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public void updateViewForInstagramLogin(InstagramLoginResponse instagramLoginResponse) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
