package in.blackpaper.instasp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.util.List;

import in.blackpaper.instasp.BuildConfig;
import in.blackpaper.instasp.GlobalConstant;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.activity.dashboard.MainActivity;
import in.blackpaper.instasp.activity.splash.SplashActivity;
import in.blackpaper.instasp.data.prefs.PreferencesManager;
import in.blackpaper.instasp.data.repositry.DataObjectRepositry;
import in.blackpaper.instasp.data.room.tables.Logins;
import in.blackpaper.instasp.utils.InstaUtils;
import in.blackpaper.instasp.utils.SomeDrawable;
import in.blackpaper.instasp.utils.ToastUtils;
import in.blackpaper.instasp.utils.ZoomstaUtil;
import in.blackpaper.instasp.view.BoldTextView;
import in.blackpaper.instasp.view.RegularButton;
import in.blackpaper.instasp.view.RegularEditText;
import in.blackpaper.instasp.view.RegularTextView;

public class LoginActivity extends AppCompatActivity {
    private RegularEditText passwordField;
    private RegularEditText usernameField;
    private String password;
    private String username;
    private RegularButton login;
    protected ProgressDialog mProgressDialog;
    private TextView termsOfUse;
    private CheckBox acceptTerms;
    private DataObjectRepositry dataObjectRepositry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dataObjectRepositry = DataObjectRepositry.dataObjectRepositry;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading,Please wait...");
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        passwordField = findViewById(R.id.password);
        usernameField = findViewById(R.id.username);
        login = findViewById(R.id.login);
        termsOfUse = findViewById(R.id.terms_of_use);
        acceptTerms = findViewById(R.id.accept_terms);

        usernameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == usernameField.getId())
                    usernameField.setCursorVisible(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (acceptTerms.isChecked()) {
                    if (!TextUtils.isEmpty(usernameField.getText().toString()) || !TextUtils.isEmpty(passwordField.getText().toString())) {
                        username = usernameField.getText().toString().trim();
                        password = passwordField.getText().toString();
                        final boolean[] isAvailable = new boolean[1];

                        LiveData<List<Logins>> loggedInUsers = dataObjectRepositry.getAllUsers();
                        loggedInUsers.observe(LoginActivity.this, new Observer<List<Logins>>() {
                            @Override
                            public void onChanged(List<Logins> logins) {
                                if (logins.size() > 0) {
                                    for (Logins logins1 : logins) {

                                        if (logins1.getUserName().equals(username)) {
                                            isAvailable[0] = true;
//                                            alreadyRegistered(logins1, username);
                                            new Sign(isAvailable[0]).execute(username);
                                            break;


                                        }
                                    }
                                    if (!isAvailable[0]) {
                                        new Sign(isAvailable[0]).execute(username);
                                    }
                                } else {
                                    new Sign(isAvailable[0]).execute(username);
                                }
                            }
                        });


                    } else {

                        Toast.makeText(LoginActivity.this, "Please enter username / password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please accept terms and conditions", Toast.LENGTH_LONG).show();
                }
            }
        });

        termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = "https://sites.google.com/view/zoomsta/privacy-policy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }

    private class Sign extends AsyncTask<String, String, String> {
        String resp;
        boolean isAvailable;

        private Sign(boolean isAvailable) {
            this.isAvailable = isAvailable;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        protected String doInBackground(String... args) {
            try {
                this.resp = InstaUtils.login(LoginActivity.this.username, LoginActivity.this.password);
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
                    ZoomstaUtil.showToast(LoginActivity.this, "User not found", 1);
                }
                if (this.resp.equals("true")) {
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getCookies(), "cooki");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getCsrf(), "csrf");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getSessionid(), "sessionid");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getUserId(), "userid");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, username, "username");

                    if (!isAvailable) {
                        //сохранение данных пользователя
                        Logins logins = new Logins();
                        logins.setUserId(InstaUtils.getUserId());
                        logins.setUserName(username);
                        logins.setProfilePic("");
                        logins.setCsrf(InstaUtils.getCsrf());
                        logins.setSession_id(InstaUtils.getSessionid());
                        logins.setCookies(InstaUtils.getCookies());
//                    logins.setProfilePic(InstaUtils.get);
//                    logins.setToken(token);
//                    logins.setBio(jsonData.getString("bio"));
//                    logins.setFullName(jsonData.getString("full_name"));
//                    logins.setMedia(jsonData.getJSONObject("counts").getInt("media"));
//                    logins.setFollows(jsonData.getJSONObject("counts").getInt("follows"));
//                    logins.setFollowedBy(jsonData.getJSONObject("counts").getInt("followed_by"));
                        long id = dataObjectRepositry.addNewUser(logins);

                    }

                    PreferencesManager.savePref(GlobalConstant.USERNAME, username);
                    PreferencesManager.savePref(GlobalConstant.USER_ID, InstaUtils.getUserId());
                    PreferencesManager.savePref(GlobalConstant.TOKEN, InstaUtils.getSessionid());
                    PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, "");


                    PreferencesManager.savePref(GlobalConstant.FULL_NAME, "");
                    PreferencesManager.savePref(GlobalConstant.BIO, "");
                    PreferencesManager.savePref(GlobalConstant.MEDIA, "");
                    PreferencesManager.savePref(GlobalConstant.FOLLOWS, "");
                    PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY, "");


                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("user", InstaUtils.getUserId());

                    hideLoading();
                    LoginActivity.this.finish();
                    LoginActivity.this.startActivity(i);
                    LoginActivity.this.overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                } else if (this.resp.equals("false")) {
                    hideLoading();
                    ZoomstaUtil.showToast(LoginActivity.this, "Incorrect Username / Password", 1);
                } else {
                    hideLoading();
                    ZoomstaUtil.showToast(LoginActivity.this, "Problem occurred logging in. Please try again", 1);
                }
            } catch (Exception e) {
                hideLoading();
                ZoomstaUtil.showToast(LoginActivity.this, "Problem occurred logging in. Please try again", 1);
            }
        }
    }

    public void showLoading() {
        if (mProgressDialog != null) {
            hideLoading();
            mProgressDialog.show();
        }
    }

    public void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    public void alreadyRegistered(Logins logins, String username) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View view2 = layoutInflaterAndroid.inflate(R.layout.item_dialog, null);
        builder.setView(view2);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        BoldTextView title = view2.findViewById(R.id.titleText);
        RegularTextView descriptionsText = view2.findViewById(R.id.descriptionText);
        title.setText(username + " already registered");
        descriptionsText.setText("Do you switch to " + username + "?");

        view2.findViewById(R.id.yes).setOnClickListener(v1 -> {
            ZoomstaUtil.setStringPreference(LoginActivity.this, logins.getCookies(), "cooki");
            ZoomstaUtil.setStringPreference(LoginActivity.this, logins.getCsrf(), "csrf");
            ZoomstaUtil.setStringPreference(LoginActivity.this, logins.getSession_id(), "sessionid");
            ZoomstaUtil.setStringPreference(LoginActivity.this, logins.getUserId(), "userid");
            ZoomstaUtil.setStringPreference(LoginActivity.this, username, "username");

            PreferencesManager.savePref(GlobalConstant.USERNAME, username);
            PreferencesManager.savePref(GlobalConstant.USER_ID, logins.getUserId());
            PreferencesManager.savePref(GlobalConstant.TOKEN, logins.getSession_id());
            PreferencesManager.savePref(GlobalConstant.PROFILE_PIC, "");


            PreferencesManager.savePref(GlobalConstant.FULL_NAME, "");
            PreferencesManager.savePref(GlobalConstant.BIO, "");
            PreferencesManager.savePref(GlobalConstant.MEDIA, "");
            PreferencesManager.savePref(GlobalConstant.FOLLOWS, "");
            PreferencesManager.savePref(GlobalConstant.FOLLOWED_BY, "");

            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.putExtra("user", InstaUtils.getUserId());

            hideLoading();
            LoginActivity.this.finish();
            LoginActivity.this.startActivity(i);
            LoginActivity.this.overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
        });
        view2.findViewById(R.id.no).setOnClickListener(v12 -> alertDialog.dismiss());


    }
}
