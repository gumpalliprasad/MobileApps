package myschoolapp.com.gsnedutech.Util;

import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public OkHttpClient getClient() {

        return  new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public Request getRequest(String url, SharedPreferences sh_Pref){
        if(url.contains(AppUrls.BASE_URL)) {
            return new Request.Builder()
                    .url(url)
                    .headers(MyUtils.addHeaders(sh_Pref))
                    .build();
        }else{
            return new Request.Builder()
                    .url(url)
                    .build();
        }
    }

    public Request postRequest(String url, RequestBody body, SharedPreferences sh_Pref){
        if(url.contains(AppUrls.BASE_URL)){
            return new Request.Builder()
                    .url(url)
                    .post(body)
                    .headers(MyUtils.addHeaders(sh_Pref))
                    .build();
        }else{
            return new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        }
    }


    public static Retrofit getAdvJeeClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS).build();


        return new Retrofit.Builder()
                .baseUrl("http://prep.myschoolapp.io:9001/")
//                .baseUrl("http://13.233.40.247:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


}
