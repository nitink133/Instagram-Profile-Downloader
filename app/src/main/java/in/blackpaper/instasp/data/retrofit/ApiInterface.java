package in.blackpaper.instasp.data.retrofit;



import com.facebook.stetho.inspector.network.ResponseBodyData;

import in.blackpaper.instasp.data.retrofit.response.InstagramLoginResponse;
import in.blackpaper.instasp.data.retrofit.response.IntagramProfileResponse;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("accounts/login/ajax/")
    Observable<InstagramLoginResponse> loginInstagram(@Field("username") String username, @Field("password") String password);


    @GET("/")
    Observable<IntagramProfileResponse> getUserProfileData(@Query("__a")String value);

}