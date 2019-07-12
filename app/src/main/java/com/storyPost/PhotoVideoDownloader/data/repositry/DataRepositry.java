package com.storyPost.PhotoVideoDownloader.data.repositry;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.storyPost.PhotoVideoDownloader.data.retrofit.response.IntagramProfileResponse;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Downloads;
import com.storyPost.PhotoVideoDownloader.data.room.tables.Logins;


public interface DataRepositry {


  LiveData<List<Logins>> getAllUsers();

    Logins getSelectedUser(int id);

    long addNewUser(Logins logins);

    int deleteExistingUser(int id);


    LiveData<List<Downloads>> getAllDownloads();


    Downloads getSelectedDownload(int id);


    long addDownloadedData(Downloads downloads);

    int deleteDownloadedData(int id);


    int checkIfUserExist(String username);


    void updateUserNameInfo(Integer id,String username,String userProfileImage);
}
