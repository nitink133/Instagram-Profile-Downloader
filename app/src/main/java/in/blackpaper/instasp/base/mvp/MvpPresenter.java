package in.blackpaper.instasp.base.mvp;

/**
 * Created by nitin on 19/05/19.
 */


/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mMvpView);

    void onDetach();

    void handleApiError(Throwable throwable);
}
