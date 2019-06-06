package com.blackpaper.InstaDownload.stories.profile.post.download.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.CommonUtils;

public class BaseActivity extends AppCompatActivity  {

    protected ProgressDialog mProgressDialog;
    protected ProgressBar progressBar;
    public Context context;
    private FragmentActivity fragmentActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentActivity = this;
        context = this;
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



}
