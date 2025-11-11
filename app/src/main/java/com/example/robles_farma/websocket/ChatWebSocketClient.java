package com.example.robles_farma.websocket;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.robles_farma.sharedpreferences.LoginStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

public class ChatWebSocketClient {
    private static final String TAG = "ChatWebSocket";
    private WebSocket webSocket;
    private final OkHttpClient client = new OkHttpClient();
    private final MutableLiveData<String> receivedMessages = new MutableLiveData<>();

    private ChatWebSocketListener.OnMessageReceivedListener messageListener;

    public LiveData<String> getReceivedMessages() {
        return receivedMessages;
    }

    public void setMessageListener(ChatWebSocketListener.OnMessageReceivedListener listener) {
        this.messageListener = listener;
    }

    // ✅ Conectar al WebSocket con token
    public void connect(Context context) {
        try {
            String jwtToken = LoginStorage.getToken(context);
            if (jwtToken == null || jwtToken.isEmpty()) {
                Log.e(TAG, "❌ Token no encontrado");
                return;
            }

            String wsUrl = "wss://codestar.space/ws?token=" + jwtToken;

            Request request = new Request.Builder()
                    .url(wsUrl)
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build();

            ChatWebSocketListener listener = new ChatWebSocketListener(message -> {
                Log.d(TAG, "📩 Mensaje recibido: " + message);
                receivedMessages.postValue(message);

                if (messageListener != null) {
                    messageListener.onMessageReceived(message);
                }
            });

            webSocket = client.newWebSocket(request, listener);
            Log.d(TAG, "✅ Conectando a WebSocket en codestar.space...");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error al conectar WebSocket: " + e.getMessage());
        }
    }

    // ✅ Enviar mensaje
    public void sendMessage(String text, String chatId, List<String> recipientIds) {
        if (webSocket == null) {
            Log.e(TAG, "❌ No hay conexión WebSocket activa");
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("text", text);
            json.put("chat_id", chatId);
            json.put("recipient_ids", new JSONArray(recipientIds));

            String jsonMessage = json.toString();
            webSocket.send(jsonMessage);
            Log.d(TAG, "📤 Mensaje enviado: " + jsonMessage);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error al crear mensaje JSON: " + e.getMessage());
        }
    }

    // ✅ Cargar historial de mensajes - MEJORADO
    public void loadChatHistory(Context context, String chatId, OnMessagesLoadedListener listener) {
        new Thread(() -> {
            try {
                String jwtToken = LoginStorage.getToken(context);
                if (jwtToken == null || jwtToken.isEmpty()) {
                    Log.e(TAG, "❌ Token no encontrado");
                    return;
                }

                // ✅ URL correcta (Railway backend)
                String url = "https://citassalud-production.up.railway.app/chats/" + chatId + "/messages/";

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + jwtToken)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        Log.d(TAG, "✅ Historial recibido: " + json);
                        listener.onMessagesLoaded(json);
                    } else {
                        Log.e(TAG, "❌ Error HTTP al obtener historial: " + response.code());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error al cargar historial: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }



    // ✅ Desconectar
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Desconectado por el usuario");
            Log.i(TAG, "🔌 WebSocket desconectado");
            webSocket = null;
        }
    }

    // 👂 Interfaz para callback
    public interface OnMessagesLoadedListener {
        void onMessagesLoaded(String jsonResponse);
    }
}