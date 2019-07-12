package com.storyPost.PhotoVideoDownloader.data.repositry;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.data.retrofit.response.IntagramProfileResponse;
import com.storyPost.PhotoVideoDownloader.data.room.dao.Downloadsdao;
import com.storyPost.PhotoVideoDownloader.data.room.dao.LoginsDao;
import com.storyPost.PhotoVideoDownloader.data.room.database.LoginsDatabase;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Downloads;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;
import io.reactivex.Observable;

public class DataObjectRepositry implements DataRepositry {


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
