package com.example.robles_farma; // Asegúrate que el paquete sea el correcto

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }

    /**
     * Devuelve el contexto de la aplicación,
     * disponible en cualquier parte del código.
     */
    public static Context getAppContext() {
        return appContext;
    }
}