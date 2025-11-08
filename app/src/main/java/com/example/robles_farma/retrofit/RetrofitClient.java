package com.example.robles_farma.retrofit;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String URL_API_SERVICE = "https://citassalud-production.up.railway.app/";
    public static String API_TOKEN;

    private static ApiService apiServiceInstance;
    private static Retrofit retrofitInstance;

    private static class AuthInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().header("Content-Type", "application/json");

            String token = API_TOKEN;
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }

    public static Retrofit getRetrofitInstance() {
        if (retrofitInstance == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor());
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(URL_API_SERVICE)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }


    public static ApiService getApiService() {
        if (apiServiceInstance == null) {
            apiServiceInstance = getRetrofitInstance().create(ApiService.class);
        }
        return apiServiceInstance;
    }
}