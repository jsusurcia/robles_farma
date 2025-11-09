package com.example.robles_farma.websocket;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatWebSocketClient extends WebSocketListener {

    private static final String TAG = "ChatWebSocket";
    private WebSocket webSocket;
    private OkHttpClient client = new OkHttpClient();

    public MutableLiveData<String> receivedMessages = new MutableLiveData<>();

    // Conectar con token JWT
    public void connect(String token) {
        String url = "ws://10.0.2.2:8000/ws?token=" + token;
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, this);
        Log.d(TAG, "Conectando al WebSocket...");
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "✅ Conectado correctamente al WebSocket");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Mensaje recibido: " + text);
        receivedMessages.postValue(text);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Error WebSocket: " + t.getMessage());
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d(TAG, "Conexión cerrada: " + reason);
    }

    public void sendMessage(String text, String chatId, List<String> recipientIds) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", text);
        payload.put("chat_id", chatId);
        payload.put("recipient_ids", recipientIds);

        String json = new Gson().toJson(payload);
        webSocket.send(json);
        Log.d(TAG, "Enviado: " + json);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Cerrando conexión desde cliente");
        }
    }
}
