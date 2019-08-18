//package com.storyPost.PhotoVideoDownloader.data.retrofit;
//
//
//
//
//import java.util.concurrent.TimeUnit;
//
//import com.storyPost.PhotoVideoDownloader.GlobalConstant;
//public class ApiClient {
//    private static Retrofit retrofit = null;
//
//
//    public static Retrofit getClient(String url) {
//        if(url.isEmpty())url = GlobalConstant.BASE_URL;
//        if (retrofit == null) {
//
//            OkHttpClient clientMe = new OkHttpClient.Builder()
//                    .connectTimeout(120, TimeUnit.SECONDS)
//                    .readTimeout(120, TimeUnit.SECONDS)
//                    .addNetworkInterceptor(new StethoInterceptor())
//                    .build();
//
//
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
//                    .client(clientMe)
//                    .addConverterFactory(JacksonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .build();
//        }
//        return retrofit;
//    }
//
//
//}
