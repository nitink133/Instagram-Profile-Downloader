package in.blackpaper.instasp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

import in.blackpaper.instasp.R;
import in.blackpaper.instasp.base.PresenterManager;
import in.blackpaper.instasp.utils.CommonUtils;

public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog mProgressDialog;
    protected ProgressBar progressBar;
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
