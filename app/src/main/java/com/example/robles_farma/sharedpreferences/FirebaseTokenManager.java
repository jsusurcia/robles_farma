package com.example.robles_farma.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class FirebaseTokenManager {

    private static final String PREF_NAME = "SHARED_PREFERENCES_AGRO_DIGITAL";
    private static final String KEY_FIREBASE_TOKEN = "firebase_token_cliente";
    private static final String KEY_FIREBASE_DISPOSITIVO = "firebase_dispositivo_cliente";

    // Método para asignar el token
    public static void setFirebaseToken(Context context, String token, String dispositivo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FIREBASE_TOKEN, token);
        editor.putString(KEY_FIREBASE_DISPOSITIVO, dispositivo);
        editor.apply();
    }

    // Método para leer el token
    public static String[] getFirebaseToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(KEY_FIREBASE_TOKEN, null);
        String dispositivo = sharedPreferences.getString(KEY_FIREBASE_DISPOSITIVO, null);
        return new String[] {token, dispositivo};
    }

}