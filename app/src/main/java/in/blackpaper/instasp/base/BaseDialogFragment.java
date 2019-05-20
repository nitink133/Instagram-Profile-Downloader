package in.blackpaper.instasp.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import in.blackpaper.instasp.R;
import in.blackpaper.instasp.base.mvp.MvpPresenter;
import in.blackpaper.instasp.base.mvp.MvpView;
import in.blackpaper.instasp.utils.DialogUitls;


public abstract class BaseDialogFragment<P extends MvpPresenter> extends DialogFragment implements MvpView {

    protected ProgressDialog mProgressDialog;
    public P mPresenter;
    public Context context;

    public abstract P createPresenter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        mPresenter = createPresenter();
        mPresenter.onAttach(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.onDetach();
        }
    }

    @Override
    public void showLoading() {
        hideLoading();
        //mProgressDialog = DialogUitls.showLoadingDialog(context);
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
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

    }
}
