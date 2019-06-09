package com.InstaDownload.stories.profile.post.download.base;


import java.lang.ref.WeakReference;
import java.net.SocketException;

import androidx.annotation.NonNull;
import com.InstaDownload.stories.profile.post.download.R;
import com.InstaDownload.stories.profile.post.download.base.mvp.MvpPresenter;
import com.InstaDownload.stories.profile.post.download.base.mvp.MvpView;
import io.reactivex.disposables.CompositeDisposable;


public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private CompositeDisposable mCompositeDisposable;

    private WeakReference<V> mMvpView;

    public BasePresenter() {
        mCompositeDisposable = new CompositeDisposable();
    }

    public interface ViewAction<V> {

        /**
         * This method will be invoked to run the action. Implement this method to interact with the view.
         *
         * @param view The reference to the view. Not null.
         */
        void run(@NonNull V view);
    }

    @Override
    public void onAttach(V mMvpView) {

        this.mMvpView = new WeakReference<V>(mMvpView);
    }

    @Override
    public void onDetach() {

        if (mMvpView != null) {
            mMvpView.clear();
            mMvpView = null;
        }

        onUnsubscribe();
    }

    @Override
    public void handleApiError(Throwable throwable) {
            if(throwable instanceof SocketException){
                getMvpView().showMessage(R.string.error_socket_exception);
            }else {
                getMvpView().showMessage(R.string.some_error);
            }
    }

    public void onUnsubscribe() {
        if (mCompositeDisposable != null)
            mCompositeDisposable.dispose();
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public V getMvpView() {
        return mMvpView == null ? null : mMvpView.get();
    }

    public boolean isViewAttached() {
        return mMvpView != null && mMvpView.get() != null;
    }

    protected final void ifViewAttached(boolean exceptionIfViewNotAttached, ViewAction<V> action) {
        final V view = mMvpView == null ? null : mMvpView.get();
        if (view != null) {
            action.run(view);
        } else if (exceptionIfViewNotAttached) {
            throw new IllegalStateException(
                    "No View attached to Presenter.");
        }
    }

    /**
     * Calls {@link #ifViewAttached(boolean, ViewAction)} with false as first parameter (don't throw
     * exception if view not attached).
     *
     * @see #ifViewAttached(boolean, ViewAction)
     */
    protected final void ifViewAttached(ViewAction<V> action) {
        ifViewAttached(false, action);
    }
}
