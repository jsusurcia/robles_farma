package com.example.robles_farma.retrofit;

import android.content.Context;
import android.util.Log;

import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://citassalud-production.up.railway.app/";
    private static Retrofit retrofit = null;

    // 🔹 Cliente con interceptores para logs y token
    private static OkHttpClient getClient(Context context) {

        // ✅ Interceptor que agrega el token
        Interceptor tokenInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header("Content-Type", "application/json");

            String token = LoginStorage.getToken(context);
            if (token != null && !token.isEmpty()) {
                // AQUÍ ESTÁ LA CORRECCIÓN: Cambiado "JWT " por "Bearer "
                builder.header("Authorization", "Bearer " + token.trim());
                Log.d("INTERCEPTOR", "✅ Token agregado al header.");
            } else {
                Log.w("INTERCEPTOR", "⚠️ No hay token guardado.");
            }

            Request request = builder.build();
            Log.d("INTERCEPTOR", "➡️ Enviando request a: " + request.url());
            Response response = chain.proceed(request);
            Log.d("INTERCEPTOR", "⬅️ Código de respuesta: " + response.code());
            return response;
        };

        // ✅ Interceptor que muestra las peticiones/respuestas en Logcat
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                message -> Log.d("HTTP", message)
        );
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // ✅ Cliente final con orden correcto de interceptores
        return new OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)                 // 🔹 primero: token
                .addInterceptor(loggingInterceptor)               // 🔹 luego: logs
                .addInterceptor(new ChuckerInterceptor(context))  // 🔹 último: depuración visual
                .build();
    }

    // 🔹 Singleton de Retrofit
    private static Retrofit getClientInstance(Context context) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(getClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // ✅ Servicio principal
    public static ApiService createService(Context context) {
        return getClientInstance(context).create(ApiService.class);
    }

    // ✅ Servicio genérico (ChatService, etc.)
    public static <T> T createService(Context context, Class<T> serviceClass) {
        return getClientInstance(context).create(serviceClass);
    }
}
