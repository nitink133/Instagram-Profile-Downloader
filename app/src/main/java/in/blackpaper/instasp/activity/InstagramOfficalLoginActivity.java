package in.blackpaper.instasp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.dashboard.MainActivity;
import in.blackpaper.instasp.activity.introscreen.IntroScreenActivity;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.room.tables.Logins;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;

public class InstagramOfficalLoginActivity extends AppCompatActivity {
    private WebView mWebView;
    private String mURL = GlobalConstant.INSTAGRAM_LOGIN_URL;
    private androidx.appcompat.widget.Toolbar toolbar;
    private SmoothProgressBar progressBar;
    private String cookies;
    private Boolean isSessionid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_offical_login);
        initUI();

    }

    public void initUI() {

        mWebView = findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true); // enable javascript
        progressBar = findViewById(R.id.progress_bar);
        startWebView();

    }


    private void startWebView() {

        mWebView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                showLoading();

                CookieManager.getInstance().removeAllCookies(null);
//                CookieManager.getInstance().flush();
                progressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressBar.setVisibility(View.GONE);


                    if (isSessionid) {
                        //сохранение данных пользователя
                        Logins logins = new Logins();
                        logins.setUserId(InstaUtils.getUserId());
                        logins.setUserName("");
                        logins.setProfilePic("");
                        logins.setToken(InstaUtils.getSessionid());
                        long id = DataObjectRepositry.dataObjectRepositry.addNewUser(logins);


                        PreferencesManager.savePref(GlobalConstant.USERNAME,"");
                        PreferencesManager.savePref(GlobalConstant.USER_ID, InstaUtils.getUserId());
                        PreferencesManager.savePref(GlobalConstant.TOKEN, InstaUtils.getSessionid());
                        PreferencesManager.savePref(GlobalConstant.PROFILE_PIC,"");


                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getCookies(), "cooki");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getCsrf(), "csrf");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getSessionid(), "sessionid");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getUserId(), "userid");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, "", "username");


                        Intent intent = new Intent(InstagramOfficalLoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("user", InstaUtils.getUserId());
                        startActivity(intent);
                        InstagramOfficalLoginActivity.this.overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                    }

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                progressBar.setVisibility(View.GONE);
                cookies = CookieManager.getInstance().getCookie(url);

                try {
                    String session_id = getCookie(url, "sessionid");
                    String csrftoken = getCookie(url, "csrftoken");
                    String userid = getCookie(url, "ds_user_id");
                    if (session_id != null && csrftoken != null && userid != null) {
                        isSessionid = true;
                        InstaUtils.setSessionId(session_id);
                        InstaUtils.setUserId(userid);
                        InstaUtils.setCookies(cookies);
                        InstaUtils.setCsrf(csrftoken, cookies);

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                ToastUtils.ErrorToast(InstagramOfficalLoginActivity.this, description);
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }


        });

        mWebView.loadUrl(mURL);


    }

    public String getCookie(String siteName, String CookieName) {
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);
        String[] temp = cookies.split(";");
        for (String ar1 : temp) {
            if (ar1.contains(CookieName)) {
                String[] temp1 = ar1.split("=");
                CookieValue = temp1[1];
                break;
            }
        }
        return CookieValue;
    }


}
