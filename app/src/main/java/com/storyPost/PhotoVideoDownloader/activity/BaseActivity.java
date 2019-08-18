package com.storyPost.PhotoVideoDownloader.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.storyPost.PhotoVideoDownloader.activity.dashboard.MainActivity;
import com.storyPost.PhotoVideoDownloader.data.prefs.PreferencesManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.utils.AppRater;
import com.storyPost.PhotoVideoDownloader.utils.CommonUtils;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BaseActivity extends AppCompatActivity  {
    private static final String TAG =BaseActivity.class.getName();
    protected ProgressDialog mProgressDialog;
    protected ProgressBar progressBar;
    public Context context;
    private FragmentActivity fragmentActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentActivity = this;
        context = this;

        AppRater.app_launched(this);
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading,Please wait...");
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

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


    public boolean isNetworkConnected() {
        return false;
    }

    public void hideKeyboard() {
        CommonUtils.hideKeyboard(context);
    }

    public void showSnackBar(int resId) {
        showSnackBar(getString(resId));
    }

    public void showSnackBar(String message) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.WHITE);
        TextView textView = (TextView) snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);

        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.BLACK);
        snackbar.show();
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    protected void onResume() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String rationale = getString(R.string.msg_rationale);
        com.nabinbhandari.android.permissions.Permissions.Options options = new com.nabinbhandari.android.permissions.Permissions.Options()
                .setRationaleDialogTitle(getString(R.string.txt_info))
                .setSettingsDialogTitle(getString(R.string.txt_warning));
        com.nabinbhandari.android.permissions.Permissions.check(BaseActivity.this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
            }
        });

        super.onResume();


    }
}
