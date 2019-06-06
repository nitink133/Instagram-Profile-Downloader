package com.blackpaper.InstaDownload.stories.profile.post.download.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.blackpaper.InstaDownload.stories.profile.post.download.R;
import com.blackpaper.InstaDownload.stories.profile.post.download.base.mvp.MvpPresenter;
import com.blackpaper.InstaDownload.stories.profile.post.download.base.mvp.MvpView;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.CommonUtils;
import com.blackpaper.InstaDownload.stories.profile.post.download.utils.DialogUitls;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public abstract class BaseActivity<P extends MvpPresenter> extends AppCompatActivity implements MvpView {

    private static final String KEY_ACTIVITY_ID = "in.unicode.acitivity.id";
    private String activityId = "";

    protected ProgressDialog mProgressDialog;
    private final static int PERMISSION_REQUEST_CODE = 100;
    protected ProgressBar progressBar;
    public P mPresenter;
    public Context context;
    public static Context contextForDailog;
    static LinearLayout cameraPermissionBottomSheet;


    public abstract P createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        contextForDailog = this;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Loading,Please wait...");
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        if (savedInstanceState != null) {
            //Activity recreated - get activity id from bundle and then presenter from presenter managet
            activityId = savedInstanceState.getString(KEY_ACTIVITY_ID);
            mPresenter = PresenterManager.getPresenter(activityId);

            if (activityId != null && mPresenter != null) {
                //Presenter Restored from cache
            } else {

                //Incase presenter or activityId is null, recreate both and save it in presenter manager
                mPresenter = createPresenter();
                activityId = UUID.randomUUID().toString();

                PresenterManager.putPresenter(activityId, mPresenter);
            }

        } else {

            //Activity is created - Create presenter and activity ID and save it in presenter managet
            mPresenter = createPresenter();
            activityId = UUID.randomUUID().toString();

            PresenterManager.putPresenter(activityId, mPresenter);
        }


        mPresenter.onAttach(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Check if activity is finishing or not
        //isChangingConfigurations() is called during orientation change, we want to save presenter in that case
        if (isFinishing() && !isChangingConfigurations()) {
            if (mPresenter != null) {
                mPresenter.onDetach();
            }

            PresenterManager.remove(activityId);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            outState.putString(KEY_ACTIVITY_ID, activityId);
        }
    }

    @Override
    public void showLoading() {
        if (mProgressDialog != null) {
            hideLoading();
            mProgressDialog.show();
        }
    }


    @Override
    public void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message) {
        if (message != null)
            DialogUitls.infoPopup(context, message, "OK");
        else
            DialogUitls.infoPopup(context, getString(R.string.some_error), "OK");
    }

    @Override
    public void showMessage(int resId) {
        onError(getString(resId));
    }


    @Override
    public void onError(int resId) {
        DialogUitls.infoPopup(context, getString(resId), "OK");
    }

    @Override
    public void onError(String message) {
        if (message != null)
            DialogUitls.infoPopup(context, message, "OK");
        else
            DialogUitls.infoPopup(context, getString(R.string.some_error), "OK");
    }

    @Override
    public boolean isNetworkConnected() {
        return false;
    }

    @Override
    public void hideKeyboard() {
        CommonUtils.hideKeyboard(context);
    }

    @Override
    public void showSnackBar(int resId) {
        showSnackBar(getString(resId));
    }

    @Override
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


    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void showDialogAndFinishActivity(String resId) {
        DialogUitls.infoPopupForCloseActivity(this, resId, "OK");
    }

    @Override
    public void showDialogAndMoveToDashborad(String resId) {
//        DialogUitls.infoPopupForDashboradActivity(this, resId, "OK");
    }

    @Override
    public void showLogoutalert() {

    }

    @Override
    public RequestBody generateStringFromRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }




}
