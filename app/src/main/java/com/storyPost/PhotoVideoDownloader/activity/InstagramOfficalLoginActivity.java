package com.storyPost.PhotoVideoDownloader.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.UrlQuerySanitizer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import com.storyPost.PhotoVideoDownloader.GlobalConstant;
import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.activity.dashboard.MainActivity;
import com.storyPost.PhotoVideoDownloader.data.prefs.PreferencesManager;
import com.storyPost.PhotoVideoDownloader.data.repositry.DataObjectRepositry;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
import com.storyPost.PhotoVideoDownloader.models.MyBridge;
import com.storyPost.PhotoVideoDownloader.utils.InstaUtils;
import com.storyPost.PhotoVideoDownloader.utils.ToastUtils;
import com.storyPost.PhotoVideoDownloader.utils.ZoomstaUtil;

public class InstagramOfficalLoginActivity extends AppCompatActivity {
    private WebView mWebView;
    private String mURL = GlobalConstant.INSTAGRAM_LOGIN_URL;
    private androidx.appcompat.widget.Toolbar toolbar;
    private SmoothProgressBar progressBar;
    private String cookies;
    private Boolean isSessionid = false;
    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_offical_login);
        initUI();

    }

    public void initUI() {

        mWebView = findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebView.addJavascriptInterface(new MyBridge(InstagramOfficalLoginActivity.this), "bridge");
        progressBar = findViewById(R.id.progress_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        startWebView();

    }


    private void startWebView() {

        mWebView.setWebViewClient(new WebViewClient() {


            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                UrlQuerySanitizer.ValueSanitizer sanitizer = UrlQuerySanitizer.getAllButNulLegal();
// remember to decide if you want the first or last parameter with the same name
// If you want the first call setPreferFirstRepeatedParameter(true);
                sanitizer.sanitize(url);
                String value = sanitizer.sanitize("username"); // get your value
                if(MyBridge.getUsername()!=null)username = MyBridge.getUsername();
               return  true;

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

                if (url.equalsIgnoreCase(mURL)) {
                    view.addJavascriptInterface(new MyBridge(InstagramOfficalLoginActivity.this), "bridge");
                    String javascript = "javascript: document.getElementsByClassName(\"_0mzm- sqdOP  L3NKy       \")[0].onclick = function() {\n" +
                            "        var username = document.getElementsByName(\"username\").value;\n" +
                            "        var password = document.getElementsByName(\"password\").value;\n" +
                            "        bridge.saveData(username, password);\n" +
                            "    };";
                    view.loadUrl(javascript);
                }


                    if (isSessionid ) {
//                        username = MyBridge.getUsername();
                        //сохранение данных пользователя
                        Logins logins = new Logins();
                        logins.setUserId(InstaUtils.getUserId());
                        logins.setUserName("");
                        logins.setProfilePic("");
                        logins.setSession_id(InstaUtils.getSessionid());
                        logins.setCooki(InstaUtils.getCookies());
                        logins.setCsrf(InstaUtils.getCsrf());

                        long id = DataObjectRepositry.dataObjectRepositry.addNewUser(logins);


                        PreferencesManager.savePref(GlobalConstant.USERNAME,username);
                        PreferencesManager.savePref(GlobalConstant.USER_ID, InstaUtils.getUserId());
                        PreferencesManager.savePref(GlobalConstant.TOKEN, InstaUtils.getSessionid());
                        PreferencesManager.savePref(GlobalConstant.PROFILE_PIC,"");


                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getCookies(), "cooki");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getCsrf(), "csrf");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getSessionid(), "sessionid");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, InstaUtils.getUserId(), "userid");
                        ZoomstaUtil.setStringPreference(InstagramOfficalLoginActivity.this, "", "username");


                        Intent intent = new Intent(InstagramOfficalLoginActivity.this, MainActivity.class);

                        PreferencesManager.savePref("isLogin",true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("user", InstaUtils.getUserId());
                        intent.putExtra("database_id",String.valueOf(id));

                        mWebView.destroy();
                        mWebView = null;
                        startActivity(intent);

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
        if(cookies!=null && !cookies.isEmpty()) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(CookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }

    private void handleReturnCode(String error) {
        System.out.println("======  handleReturnCode error ====" + error);
        Intent intent = new Intent();
        if (error == null) {
            intent.putExtra("username", MyBridge.getUsername());
            intent.putExtra("password", MyBridge.getPassword());
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }
        finish();

    }




}
