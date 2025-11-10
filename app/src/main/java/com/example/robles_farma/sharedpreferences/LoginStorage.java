package com.example.robles_farma.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.example.robles_farma.response.PacienteResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Clase que maneja la sesión del usuario, token JWT y datos del paciente.
 * Incluye validación automática del token y limpieza segura.
 */
public class LoginStorage {

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_DNI = "dni";
    private static final String KEY_CLAVE = "clave";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_PACIENTE = "paciente";
    private static final String KEY_USER_ID = "user_id"; // 👈 Nuevo campo
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    private final SharedPreferences prefs;
    private final Gson gson;

    public LoginStorage(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // 🔹 Guardar login completo (cuando el usuario inicia sesión)
    public void saveLoginCredentials(String dni, String clave, String token, PacienteResponse paciente) {
        prefs.edit()
                .putString(KEY_DNI, dni)
                .putString(KEY_CLAVE, clave)
                .putString(KEY_TOKEN, token)
                .putString(KEY_PACIENTE, gson.toJson(paciente))
                .putString(KEY_USER_ID, paciente != null ? paciente.getId() : null) // 👈 Guardar ID del paciente
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    // 🔹 Guardar sesión sin credenciales (ej. si se revalida token)
    public void saveSession(String token, PacienteResponse paciente) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_PACIENTE, gson.toJson(paciente))
                .putString(KEY_USER_ID, paciente != null ? paciente.getId() : null)
                .putBoolean(KEY_LOGGED_IN, true)
                .apply();
    }

    // 🔹 Limpiar todo (logout completo o token expirado)
    public void clearLoginCredentials() {
        prefs.edit().clear().apply();
        Log.w("LoginStorage", "Sesión eliminada.");
    }

    // 🔹 Verifica si el usuario está logueado y el token sigue activo
    public boolean isUserLoggedIn() {
        boolean loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);
        String token = prefs.getString(KEY_TOKEN, null);

        if (!loggedIn || token == null) return false;

        if (!isTokenValid()) {
            Log.w("LoginStorage", "⚠️ Token expirado, cerrando sesión automáticamente");
            clearLoginCredentials();
            return false;
        }

        return true;
    }

    // 🔹 Valida si el token JWT está vigente
    public boolean isTokenValid() {
        String token = prefs.getString(KEY_TOKEN, null);
        if (token == null || token.isEmpty()) {
            Log.e("LoginStorage", "❌ Token no encontrado");
            return false;
        }

        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return false;

            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP));
            JSONObject json = new JSONObject(payload);
            long exp = json.optLong("exp", 0);
            long now = System.currentTimeMillis() / 1000;

            boolean valid = exp > now;
            if (!valid) {
                Log.w("LoginStorage", "Token expirado hace " + (now - exp) + "s");
            } else {
                Log.i("LoginStorage", "Token válido. Expira en " + (exp - now) + "s");
            }
            return valid;

        } catch (Exception e) {
            Log.e("LoginStorage", "Error al validar token: " + e.getMessage());
            return false;
        }
    }

    // 🔹 Getters
    public String getDni() { return prefs.getString(KEY_DNI, null); }
    public String getClave() { return prefs.getString(KEY_CLAVE, null); }
    public String getToken() { return prefs.getString(KEY_TOKEN, null); }

    public PacienteResponse getPaciente() {
        String json = prefs.getString(KEY_PACIENTE, null);
        return json != null ? gson.fromJson(json, PacienteResponse.class) : null;
    }

    // ✅ Nuevo getter del userId
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    // 🔹 Métodos estáticos (para accesos rápidos)
    public static void saveToken(Context context, String token) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null);
    }

    // ✅ Nuevo getter estático para el ID del usuario
    public static String getUserId(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_USER_ID, null);
    }
}
