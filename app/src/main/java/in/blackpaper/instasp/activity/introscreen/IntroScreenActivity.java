package in.blackpaper.instasp.activity.introscreen;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.dashboard.MainActivity;
import in.blackpaper.instasp.adapter.IntroScreenAdapter;
import in.blackpaper.instasp.base.BaseActivity;
import in.blackpaper.instasp.contractor.IntroScreenContractor;
import in.blackpaper.instasp.data.localpojo.IntroScreens;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import in.blackpaper.instasp.dialog.AuthenticationDialog;
import in.blackpaper.instasp.listener.AuthenticationListener;
import in.blackpaper.instasp.utils.Utility;

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
                authenticationDialog.show(getSupportFragmentManager().beginTransaction(), AuthenticationDialog.TAG);
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
                        Logins logins = new Logins();
                        logins.setUserId(jsonData.getString("id"));
                        logins.setUserName(jsonData.getString("username"));
                        logins.setProfilePic(jsonData.getString("profile_picture"));
                        logins.setToken(token);
                        logins.setBio(jsonData.getString("bio"));
                        logins.setFullName(jsonData.getString("full_name"));
                        logins.setMedia(jsonData.getJSONObject("counts").getInt("media"));
                        logins.setFollows(jsonData.getJSONObject("counts").getInt("follows"));
                        logins.setFollowedBy(jsonData.getJSONObject("counts").getInt("followed_by"));
                        long id = mPresenter.addNewUser(logins);


                        PreferencesManager.savePref(GlobalConstant.USERNAME, jsonData.getString("username"));
                        PreferencesManager.savePref(GlobalConstant.USER_ID, jsonData.getString("id"));
                        PreferencesManager.savePref(GlobalConstant.TOKEN, token);
                        PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, jsonData.getString("profile_picture"));


                        PreferencesManager.savePref(GlobalConstant.FULL_NAME, jsonData.getString("full_name"));
                        PreferencesManager.savePref(GlobalConstant.BIO, jsonData.getString("bio"));
                        PreferencesManager.savePref(GlobalConstant.MEDIA, jsonData.getJSONObject("counts").getInt("media"));
                        PreferencesManager.savePref(GlobalConstant.FOLLOWS,jsonData.getJSONObject("counts").getInt("follows"));
                        PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY, jsonData.getJSONObject("counts").getInt("followed_by"));



                        Intent intent = new Intent(IntroScreenActivity.this, MainActivity.class);
                        intent.putExtra("login_id", id);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.some_error), Toast.LENGTH_LONG);
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
