package in.blackpaper.instasp.data.repositry;

import in.blackpaper.instasp.data.retrofit.ApiClient;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.http.Query;

public interface DataRepositry {


    Retrofit getApiClient();

    Observable<InstagramLoginResponse> loginInstagram( String username, String password);
}
