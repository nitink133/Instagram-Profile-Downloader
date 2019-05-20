package in.blackpaper.instasp.data.repositry;

import in.blackpaper.instasp.data.retrofit.ApiClient;
import in.blackpaper.instasp.data.retrofit.ApiInterface;
import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import io.reactivex.Observable;
import retrofit2.Retrofit;

public class DataObjectRepositry implements DataRepositry {
    @Override
    public Retrofit getApiClient() {
        return ApiClient.getClient();
    }

    @Override
    public Observable<InstagramLoginResponse> loginInstagram(String username, String password) {

        return getApiClient().create(ApiInterface.class).loginInstagram(username, password);
    }
}
