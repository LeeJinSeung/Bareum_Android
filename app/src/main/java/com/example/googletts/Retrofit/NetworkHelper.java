package com.example.googletts.Retrofit;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHelper {
    private Retrofit retrofit;
    private ApiService apiService;
    public static NetworkHelper networkHelper = new NetworkHelper();
    private Dispatcher dispatcher;

    public NetworkHelper() {
        dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);
        // OkHttpClient httpClient = new OkHttpClient.Builder().dispatcher(dispatcher).build();
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().dispatcher(dispatcher);

//
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                final Request copy = chain.request().newBuilder().build();
//                final Buffer buffer = new Buffer();
//                copy.body().writeTo(buffer);
//                Log.e("OkHTTP Interceptor", buffer.readUtf8());
//                Request request = chain.request().newBuilder().addHeader(Config.API_KEY_HEADER, Config.API_KEY).build();
//                return chain.proceed(request);
//            }
//        });
        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://121.131.17.243:5000")
                .client(httpClient.build())
                .build();

        apiService = retrofit.create(ApiService.class); //실제 api Method들이선언된 Interface객체 선언
    }

    public static NetworkHelper getInstance() { //싱글톤으로 선언된 레트로핏 객체 얻는 용
        return networkHelper;
    }

    public ApiService getApiService() { // API Interface 객체 얻는 용
        return apiService;
    }
}
