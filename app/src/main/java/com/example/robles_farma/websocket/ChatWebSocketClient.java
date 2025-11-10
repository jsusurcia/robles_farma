package com.example.robles_farma.websocket;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatWebSocketClient extends WebSocketListener {

    private static final String TAG = "ChatWebSocket";
    private static final String BASE_WS_URL = "wss://codestar.space/ws?token=";

    private WebSocket webSocket;
    private final MutableLiveData<String> receivedMessages = new MutableLiveData<>();

    public MutableLiveData<String> getReceivedMessages() {
        return receivedMessages;
    }

    // 🔹 Conecta al servidor con el token JWT que ya tienes guardado
    public void connect(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .pingInterval(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        // Enviar el token como HEADER en lugar de query string
        Request request = new Request.Builder()
                .url("wss://codestar.space/ws")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        webSocket = client.newWebSocket(request, this);
        Log.d(TAG, "Conectando a WebSocket en codestar.space...");
    }


    // 🔹 Enviar mensaje al backend
    public void sendMessage(String text, String chatId, List<String> recipientIds) {
        try {
            JSONObject json = new JSONObject();
            json.put("text", text);
            json.put("chat_id", chatId);
            JSONArray recipients = new JSONArray(recipientIds);
            json.put("recipient_ids", recipients);

            if (webSocket != null) {
                webSocket.send(json.toString());
                Log.d(TAG, "Mensaje enviado: " + json);
            } else {
                Log.e(TAG, "WebSocket no inicializado");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creando JSON", e);
        }
    }

    // 🔹 Cerrar conexión
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Cierre voluntario");
            webSocket = null;
            Log.d(TAG, "WebSocket desconectado");
        }
    }

    // Eventos del WebSocket
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d(TAG, "Conectado a codestar.space");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Mensaje recibido: " + text);
        receivedMessages.postValue(text);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.w(TAG, "⚠Cerrando conexión: " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Error en WebSocket: " + t.getMessage());
    }
}
