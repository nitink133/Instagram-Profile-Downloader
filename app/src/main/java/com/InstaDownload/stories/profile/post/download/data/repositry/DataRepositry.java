package com.InstaDownload.stories.profile.post.download.data.repositry;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.InstaDownload.stories.profile.post.download.data.retrofit.response.InstagramLoginResponse;
import com.InstaDownload.stories.profile.post.download.data.retrofit.response.IntagramProfileResponse;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Downloads;
import com.InstaDownload.stories.profile.post.download.data.room.tables.Logins;
import io.reactivex.Observable;
import retrofit2.Retrofit;

public interface DataRepositry {


    Retrofit getApiClient(String url);

    Observable<InstagramLoginResponse> loginInstagram(String url, String username, String password);

    LiveData<List<Logins>> getAllUsers();

    Logins getSelectedUser(int id);

    long addNewUser(Logins logins);

    int deleteExistingUser(int id);

    Observable<IntagramProfileResponse> getUserProfileData(String url);

    LiveData<List<Downloads>> getAllDownloads();


    Downloads getSelectedDownload(int id);


    long addDownloadedData(Downloads downloads);

    int deleteDownloadedData(int id);


    int checkIfUserExist(String username);


    void updateUserNameInfo(Integer id,String username,String userProfileImage);
}
