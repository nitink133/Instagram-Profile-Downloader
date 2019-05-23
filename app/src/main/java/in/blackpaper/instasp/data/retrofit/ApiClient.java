package in.blackpaper.instasp.data.retrofit;


import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import in.blackpaper.instasp.GlobalConstant;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class ApiClient {
    private static Retrofit retrofit = null;


    public static Retrofit getClient(String url) {
        if(url.isEmpty())url = GlobalConstant.BASE_URL;
        if (retrofit == null) {

            OkHttpClient clientMe = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(clientMe)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
