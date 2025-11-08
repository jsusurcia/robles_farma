package com.example.robles_farma.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.robles_farma.response.PacienteResponse;
import com.google.gson.Gson;

public class LoginStorage {
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_DNI = "dni";
    private static final String KEY_CLAVE = "clave";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PACIENTE = "paciente";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public LoginStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveLoginCredentials(String dni, String clave, String token, PacienteResponse paciente) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DNI, dni);
        editor.putString(KEY_CLAVE, clave);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_PACIENTE, gson.toJson(paciente));
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.apply();
    }

    public void saveSession(String token, PacienteResponse paciente) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_PACIENTE, gson.toJson(paciente));
        // No guardamos isLoggedIn para que no se recuerden las credenciales
        editor.remove(KEY_DNI);
        editor.remove(KEY_CLAVE);
        editor.remove(KEY_LOGGED_IN);
        editor.apply();
    }

    public void clearLoginCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getDni() {
        return sharedPreferences.getString(KEY_DNI, null);
    }

    public String getClave() {
        return sharedPreferences.getString(KEY_CLAVE, null);
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public PacienteResponse getPaciente() {
        String pacienteJson = sharedPreferences.getString(KEY_PACIENTE, null);
        if (pacienteJson != null) {
            return gson.fromJson(pacienteJson, PacienteResponse.class);
        }
        return null;
    }
}
