package com.InstaDownload.stories.profile.post.download.data.repositry;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.InstaDownload.stories.profile.post.download.data.retrofit.ApiClient;
import com.InstaDownload.stories.profile.post.download.data.retrofit.ApiInterface;
import com.InstaDownload.stories.profile.post.download.data.retrofit.response.InstagramLoginResponse;
import com.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.InstaDownload.stories.profile.post.download.data.room.dao.Downloadsdao;
import com.InstaDownload.stories.profile.post.download.data.room.dao.LoginsDao;
import com.InstaDownload.stories.profile.post.download.data.room.database.LoginsDatabase;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Downloads;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;
import io.reactivex.Observable;
import retrofit2.Retrofit;

public class DataObjectRepositry implements DataRepositry {
    @Override
    public Retrofit getApiClient(String url) {
        return ApiClient.getClient(url);
    }

    LoginsDatabase mDatabase;
    LoginsDao mLoginsDao;
    Downloadsdao downloadsdao;
    private LiveData<List<Logins>> loginsDataList = null;
    public static DataObjectRepositry dataObjectRepositry=null;

    public DataObjectRepositry(Application application) {

        mDatabase = mDatabase.getDatabase(application);
        mLoginsDao = mDatabase.loginsDao();
        downloadsdao = mDatabase.downloadsdao();
        loginsDataList = mLoginsDao.getAllUsers();

    }

    public static void init(Application application) {
        String PREF_NAME = application.getPackageName();
        if (dataObjectRepositry == null) {
              dataObjectRepositry = new DataObjectRepositry(application);

        } else {
            throw new RuntimeException("DataObjectRepository not initialized");
        }
    }

    @Override
    public Observable<InstagramLoginResponse> loginInstagram(String url,String username, String password) {

        return getApiClient(url).create(ApiInterface.class).loginInstagram(username, password);
    }

    @Override
    public LiveData<List<Logins>> getAllUsers() {
        if (loginsDataList == null) {
            loginsDataList = mLoginsDao.getAllUsers();
        }

        return loginsDataList;
    }

    @Override
    public Logins getSelectedUser(int id) {
        return mLoginsDao.getSelectedUser(id);
    }

    @Override
    public long addNewUser(Logins logins) {
        return mLoginsDao.insert(logins);
    }

    @Override
    public int deleteExistingUser(int id) {
        return mLoginsDao.delete(id);
    }

    @Override
    public Observable<IntagramProfileResponse> getUserProfileData(String url) {
        return getApiClient(url).create(ApiInterface.class).getUserProfileData("1");
    }

    @Override
    public LiveData<List<Downloads>> getAllDownloads() {
        return downloadsdao.getAllDownloads();
    }

    @Override
    public Downloads getSelectedDownload(int id) {
        return downloadsdao.getSelectedDownload(id);
    }

    @Override
    public long addDownloadedData(Downloads downloads) {
        return downloadsdao.insert(downloads);
    }

    @Override
    public int deleteDownloadedData(int id) {
        return downloadsdao.delete(id);
    }

    @Override
    public int checkIfUserExist(String username) {
        return mLoginsDao.checkIfUserExist(username);
    }

    @Override
    public void updateUserNameInfo(Integer id, String username, String userProfileImage) {
        mLoginsDao.updateUserNameInfo(id,username,userProfileImage);
    }
}
