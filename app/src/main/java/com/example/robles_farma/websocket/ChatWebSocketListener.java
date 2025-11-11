package com.example.robles_farma.websocket;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatWebSocketListener extends WebSocketListener {
    private static final String TAG = "ChatWebSocketListener";

    // 👂 Interfaz para comunicar mensajes al Fragment
    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
    }

    private OnMessageReceivedListener listener;

    public ChatWebSocketListener(OnMessageReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.i(TAG, "✅ WebSocket conectado con éxito");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "📩 Mensaje recibido RAW: " + text);

        // ✅ ENVIAR TODO al Fragment sin filtrar
        // El Fragment decidirá si mostrarlo o no
        if (listener != null) {
            listener.onMessageReceived(text);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(TAG, "📩 Mensaje binario recibido: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.w(TAG, "⚠️ WebSocket cerrándose: " + reason);
        webSocket.close(1000, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "❌ Error en WebSocket: " + t.getMessage());
    }
}