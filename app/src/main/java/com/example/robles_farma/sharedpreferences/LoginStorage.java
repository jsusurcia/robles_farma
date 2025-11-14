package com.example.robles_farma.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.example.robles_farma.response.PacienteResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Map;

public class LoginStorage {
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_DNI = "dni";
    private static final String KEY_CLAVE = "clave";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PACIENTE = "paciente";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    private static final String KEY_REMEMBER_ME = "rememberMe";

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
        editor.putInt(KEY_USER_ID, paciente.getIdPaciente());
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putBoolean(KEY_REMEMBER_ME, true);
        editor.apply();
    }

    public void saveSession(String token, PacienteResponse paciente) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_PACIENTE, gson.toJson(paciente));
        editor.putInt(KEY_USER_ID, paciente.getIdPaciente());
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putBoolean(KEY_REMEMBER_ME, false);
        editor.apply();
    }

    public void clearLoginCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     *  MÉTODO MEJORADO: Verifica si hay sesión activa Y si el token NO está expirado
     */
    public boolean isUserLoggedIn() {
        boolean hasCredentials = sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
        boolean hasToken = sharedPreferences.getString(KEY_TOKEN, null) != null;

        // Si no hay ni credenciales ni token, retornar false
        if (!hasCredentials && !hasToken) {
            return false;
        }

        // Validar si el token NO está expirado
        boolean tokenValid = isTokenValid();

        if (!tokenValid) {
            Log.w("LoginStorage", "Token expirado, limpiando sesión automáticamente");
            clearLoginCredentials(); // Limpiar todo si el token expiró
            return false;
        }

        return true; // Token válido y sesión activa
    }

    /**
     *  NUEVO: Valida si el token JWT NO está expirado
     */
    public boolean isTokenValid() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);

        if (token == null || token.isEmpty()) {
            Log.e("LoginStorage", " No hay token guardado");
            return false;
        }

        try {
            // Separar el JWT en sus 3 partes: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                Log.e("LoginStorage", " Token malformado (no tiene 3 partes)");
                return false;
            }

            // Decodificar el payload (parte 2) que contiene el "exp"
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP));
            JSONObject json = new JSONObject(payload);

            // Obtener el tiempo de expiración (exp) en segundos
            long exp = json.getLong("exp");
            long currentTime = System.currentTimeMillis() / 1000; // Tiempo actual en segundos

            Log.d("LoginStorage", " Token expira en: " + exp + " (epoch)");
            Log.d("LoginStorage", " Tiempo actual: " + currentTime + " (epoch)");
            Log.d("LoginStorage", " Tiempo restante: " + (exp - currentTime) + " segundos");

            // El token es válido si exp > currentTime
            boolean isValid = exp > currentTime;

            if (!isValid) {
                Log.e("LoginStorage", " Token EXPIRADO hace " + (currentTime - exp) + " segundos");
            } else {
                Log.i("LoginStorage", " Token VÁLIDO, expira en " + (exp - currentTime) + " segundos");
            }

            return isValid;

        } catch (Exception e) {
            Log.e("LoginStorage", " Error al validar token: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isRememberMeEnabled() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    public String getDni() {
        return sharedPreferences.getString(KEY_DNI, null);
    }

    public String getClave() {
        return sharedPreferences.getString(KEY_CLAVE, null);
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public PacienteResponse getPaciente() {
        String pacienteJson = sharedPreferences.getString(KEY_PACIENTE, null);
        if (pacienteJson != null) {
            return gson.fromJson(pacienteJson, PacienteResponse.class);
        }
        return null;
    }

    public static String getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Obtenemos el ID guardado (que es un Int)
        // Usamos -1 como valor por defecto si no se encuentra
        int id = prefs.getInt(KEY_USER_ID, -1);

        if (id == -1) {
            // Si no hay ID, devolvemos null
            return null;
        }
        return String.valueOf(id);
    }

    public interface DoctorNamesCallback {
        void onLoaded(Map<String, String> nombres);
    }

}