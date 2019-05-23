package in.blackpaper.instasp.data.repositry;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;

import java.util.List;

import in.blackpaper.instasp.data.retrofit.ApiClient;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.retrofit.response.IntagramProfileResponse;
import in.blackpaper.instasp.data.room.tables.Logins;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Query;

public interface DataRepositry {


    Retrofit getApiClient(String url);

    Observable<InstagramLoginResponse> loginInstagram(String url, String username, String password);

    LiveData<List<Logins>> getAllUsers();

    Logins getSelectedUser(int id);

    long addNewUser(Logins logins);

    int deleteExistingUser(int id);

    Observable<IntagramProfileResponse> getUserProfileData(String url);
}
