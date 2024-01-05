package myschoolapp.com.gsnedutech.JeeMains.Utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JeeApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45,TimeUnit.SECONDS).build();



        retrofit = new Retrofit.Builder()
                .baseUrl("http://prep.myschoolapp.io:3000/")
//                .baseUrl("http://13.233.117.241:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }


    public static Retrofit getAuthClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45,TimeUnit.SECONDS).build();


        retrofit = new Retrofit.Builder()
                .baseUrl("http://13.232.61.201:3001/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

}
