package com.example.robles_farma.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robles_farma.R;
import com.example.robles_farma.adapter.ChatMessagesAdapter;
import com.example.robles_farma.model.ChatMessage;
import com.example.robles_farma.sharedpreferences.LoginStorage;
import com.example.robles_farma.websocket.ChatWebSocketClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatsFragment extends Fragment {

    private static final String TAG = "ChatsFragment";
    private ChatWebSocketClient wsClient;
    private EditText etMessage;
    private ImageButton btnSend;
    private RecyclerView recyclerViewMessages;
    private ChatMessagesAdapter adapter;

    private String chatId;
    private String doctorId;
    private String doctorName;
    private String currentUserId;

    private final Set<String> shownMessageIds = new HashSet<>();
    private boolean isHistoryLoaded = false;
    private boolean isViewCreated = false;

    private static final String KEY_CHAT_ID = "chatId";
    private static final String KEY_DOCTOR_ID = "doctorId";
    private static final String KEY_DOCTOR_NAME = "doctorName";

    public ChatsFragment() {
        // Constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        android.util.Log.d(TAG, "════════════════════════════════════════");
        android.util.Log.d(TAG, "🎬 onViewCreated INICIADO");
        android.util.Log.d(TAG, "════════════════════════════════════════");

        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        TextView chatHeader = view.findViewById(R.id.tvChatHeader);

        adapter = new ChatMessagesAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(adapter);

        // ✅ Obtener ID del usuario actual
        currentUserId = String.valueOf(LoginStorage.getUserId(requireContext()));
        android.util.Log.d(TAG, "🆔 currentUserId: '" + currentUserId + "'");

        // ✅ RECUPERAR DATOS
        if (savedInstanceState != null) {
            chatId = savedInstanceState.getString(KEY_CHAT_ID);
            doctorId = savedInstanceState.getString(KEY_DOCTOR_ID);
            doctorName = savedInstanceState.getString(KEY_DOCTOR_NAME);
            android.util.Log.d(TAG, "🔄 Datos desde savedInstanceState:");
        } else if (getArguments() != null) {
            chatId = getArguments().getString(KEY_CHAT_ID);
            doctorId = getArguments().getString(KEY_DOCTOR_ID);
            doctorName = getArguments().getString(KEY_DOCTOR_NAME);
            android.util.Log.d(TAG, "📦 Datos desde getArguments():");
        }

        android.util.Log.d(TAG, "  - chatId: '" + chatId + "'");
        android.util.Log.d(TAG, "  - doctorId: '" + doctorId + "'");
        android.util.Log.d(TAG, "  - doctorName: '" + doctorName + "'");
        android.util.Log.d(TAG, "════════════════════════════════════════");

        chatHeader.setText(doctorName != null ? doctorName : "Doctor");

        // ✅ Inicializar WebSocket SOLO la primera vez
        if (!isViewCreated) {
            wsClient = new ChatWebSocketClient();
            wsClient.connect(requireContext());
            isViewCreated = true;
        }

        // 🔥 OBSERVAR MENSAJES EN TIEMPO REAL
        wsClient.getReceivedMessages().observe(getViewLifecycleOwner(), raw -> {
            try {
                android.util.Log.d(TAG, "");
                android.util.Log.d(TAG, "╔═══════════════════════════════════════════╗");
                android.util.Log.d(TAG, "║   MENSAJE EN TIEMPO REAL RECIBIDO         ║");
                android.util.Log.d(TAG, "╚═══════════════════════════════════════════╝");
                android.util.Log.d(TAG, "📡 RAW: " + raw);

                JSONObject obj = new JSONObject(raw);

                // ✅ Extraer todos los campos
                String messageId = obj.optString("id", String.valueOf(System.currentTimeMillis()));
                String text = obj.optString("text", "");
                String timestamp = obj.optString("timestamp", getCurrentTimestamp());
                String senderRol = obj.optString("sender_rol", "");

                // Extraer sender_id de múltiples formas posibles
                String senderId = "";
                if (obj.has("sender_id") && !obj.isNull("sender_id")) {
                    Object senderIdObj = obj.opt("sender_id");
                    if (senderIdObj != null) {
                        senderId = String.valueOf(senderIdObj);
                    }
                }

                android.util.Log.d(TAG, "📝 Datos parseados:");
                android.util.Log.d(TAG, "  ├─ id: '" + messageId + "'");
                android.util.Log.d(TAG, "  ├─ text: '" + text + "'");
                android.util.Log.d(TAG, "  ├─ sender_id: '" + senderId + "' (tipo: " + (senderId.isEmpty() ? "vacío" : senderId.getClass().getSimpleName()) + ")");
                android.util.Log.d(TAG, "  ├─ sender_rol: '" + senderRol + "'");
                android.util.Log.d(TAG, "  └─ timestamp: '" + timestamp + "'");

                // Evitar duplicados
                if (shownMessageIds.contains(messageId)) {
                    android.util.Log.d(TAG, "⏭️  MENSAJE DUPLICADO - Ignorado");
                    android.util.Log.d(TAG, "╚═══════════════════════════════════════════╝");
                    return;
                }
                shownMessageIds.add(messageId);

                android.util.Log.d(TAG, "");
                android.util.Log.d(TAG, "🔍 COMPARACIÓN DE IDs:");
                android.util.Log.d(TAG, "  ├─ currentUserId:  '" + currentUserId + "'");
                android.util.Log.d(TAG, "  ├─ doctorId:       '" + doctorId + "'");
                android.util.Log.d(TAG, "  └─ senderId:       '" + senderId + "'");

                // 🔥 LÓGICA SIMPLIFICADA Y ROBUSTA
                boolean isSentByMe = false;
                String senderName = "Desconocido";

                // Verificación 1: ¿Soy yo?
                if (!senderId.isEmpty() && senderId.equals(currentUserId)) {
                    isSentByMe = true;
                    senderName = "Tú";
                    android.util.Log.d(TAG, "✅ RESULTADO: Es MI mensaje (senderId == currentUserId)");
                }
                // Verificación 2: ¿Es personal médico por ROL?
                else if (senderRol.equalsIgnoreCase("personal_medico")) {
                    isSentByMe = false;
                    senderName = (doctorName != null && !doctorName.isEmpty()) ? doctorName : "Doctor";
                    android.util.Log.d(TAG, "👨‍⚕️ RESULTADO: Es del DOCTOR (por sender_rol = 'personal_medico')");
                }
                // Verificación 3: ¿Coincide con doctorId?
                else if (doctorId != null && !doctorId.isEmpty() && !senderId.isEmpty() && senderId.equals(doctorId)) {
                    isSentByMe = false;
                    senderName = (doctorName != null && !doctorName.isEmpty()) ? doctorName : "Doctor";
                    android.util.Log.d(TAG, "👨‍⚕️ RESULTADO: Es del DOCTOR (senderId == doctorId)");
                }
                // 🔥 NUEVA LÓGICA: Si no soy yo, y tiene senderId, entonces ES EL DOCTOR
                else if (!senderId.isEmpty() && !senderId.equals(currentUserId)) {
                    isSentByMe = false;
                    senderName = (doctorName != null && !doctorName.isEmpty()) ? doctorName : "Doctor";
                    android.util.Log.d(TAG, "👨‍⚕️ RESULTADO: Es del DOCTOR (senderId != currentUserId, chat 1-a-1)");
                }
                // Por defecto
                else {
                    isSentByMe = false;
                    senderName = "Otro participante";
                    android.util.Log.d(TAG, "❓ RESULTADO: Participante DESCONOCIDO");
                }

                android.util.Log.d(TAG, "");
                android.util.Log.d(TAG, "📌 DECISIÓN FINAL:");
                android.util.Log.d(TAG, "  ├─ isSentByMe: " + isSentByMe);
                android.util.Log.d(TAG, "  └─ senderName: '" + senderName + "'");
                android.util.Log.d(TAG, "╚═══════════════════════════════════════════╝");
                android.util.Log.d(TAG, "");

                ChatMessage message = new ChatMessage(
                        messageId,
                        text,
                        senderId,
                        senderName,
                        timestamp,
                        isSentByMe
                );

                requireActivity().runOnUiThread(() -> {
                    adapter.addMessage(message);
                    recyclerViewMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                });

            } catch (JSONException e) {
                android.util.Log.e(TAG, "❌ Error parseando JSON: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Enviar mensaje
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                wsClient.sendMessage(message, chatId, Arrays.asList(currentUserId, doctorId));
                etMessage.setText("");
                android.util.Log.d(TAG, "📤 Mensaje enviado - Chat: " + chatId);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CHAT_ID, chatId);
        outState.putString(KEY_DOCTOR_ID, doctorId);
        outState.putString(KEY_DOCTOR_NAME, doctorName);
        android.util.Log.d(TAG, "💾 Estado guardado en Bundle");
    }

    private void loadChatHistory() {
        android.util.Log.d(TAG, "");
        android.util.Log.d(TAG, "╔═══════════════════════════════════════════╗");
        android.util.Log.d(TAG, "║      CARGANDO HISTORIAL DE CHAT           ║");
        android.util.Log.d(TAG, "╚═══════════════════════════════════════════╝");
        android.util.Log.d(TAG, "📥 Chat ID: " + chatId);

        wsClient.loadChatHistory(requireContext(), chatId, json -> {
            try {
                android.util.Log.d(TAG, "📨 Historial recibido (RAW): " + json);
                JSONArray messagesArray = new JSONArray(json);

                List<ChatMessage> historyMessages = new ArrayList<>();

                requireActivity().runOnUiThread(() -> {
                    shownMessageIds.clear();
                    adapter.setMessages(new ArrayList<>());

                    if (messagesArray.length() == 0) {
                        android.util.Log.w(TAG, "⚠️  No hay mensajes en el historial");
                        isHistoryLoaded = true;
                        return;
                    }

                    android.util.Log.d(TAG, "📜 Procesando " + messagesArray.length() + " mensajes del historial...");

                    for (int i = 0; i < messagesArray.length(); i++) {
                        JSONObject msg = messagesArray.optJSONObject(i);
                        if (msg != null) {
                            String messageId = msg.optString("id", String.valueOf(i));
                            String text = msg.optString("text", "");
                            String timestamp = msg.optString("timestamp", "");
                            String senderRol = msg.optString("sender_rol", "");

                            String senderId = "";
                            if (msg.has("sender_id") && !msg.isNull("sender_id")) {
                                Object senderIdObj = msg.opt("sender_id");
                                if (senderIdObj != null) {
                                    senderId = String.valueOf(senderIdObj);
                                }
                            }

                            shownMessageIds.add(messageId);

                            android.util.Log.d(TAG, "");
                            android.util.Log.d(TAG, "  ┌─ Mensaje #" + (i + 1) + " ───────────");
                            android.util.Log.d(TAG, "  │ sender_id: '" + senderId + "'");
                            android.util.Log.d(TAG, "  │ sender_rol: '" + senderRol + "'");
                            android.util.Log.d(TAG, "  │ text: '" + text + "'");

                            boolean isSentByMe = false;
                            String senderName = "Desconocido";

                            if (!senderId.isEmpty() && senderId.equals(currentUserId)) {
                                isSentByMe = true;
                                senderName = "Tú";
                                android.util.Log.d(TAG, "  └─ ✅ Mi mensaje");
                            } else if (senderRol.equalsIgnoreCase("personal_medico")) {
                                isSentByMe = false;
                                senderName = (doctorName != null) ? doctorName : "Doctor";
                                android.util.Log.d(TAG, "  └─ 👨‍⚕️ Mensaje del doctor (por rol)");
                            } else if (doctorId != null && !doctorId.isEmpty() && !senderId.isEmpty() && senderId.equals(doctorId)) {
                                isSentByMe = false;
                                senderName = (doctorName != null) ? doctorName : "Doctor";
                                android.util.Log.d(TAG, "  └─ 👨‍⚕️ Mensaje del doctor (por ID)");
                            } else if (!senderId.isEmpty() && !senderId.equals(currentUserId)) {
                                // 🔥 NUEVA LÓGICA: Si no soy yo, es el doctor (chat 1-a-1)
                                isSentByMe = false;
                                senderName = (doctorName != null) ? doctorName : "Doctor";
                                android.util.Log.d(TAG, "  └─ 👨‍⚕️ Mensaje del doctor (chat 1-a-1)");
                            } else {
                                isSentByMe = false;
                                senderName = "Otro participante";
                                android.util.Log.d(TAG, "  └─ ❓ Desconocido");
                            }

                            ChatMessage chatMessage = new ChatMessage(
                                    messageId,
                                    text,
                                    senderId,
                                    senderName,
                                    timestamp,
                                    isSentByMe
                            );

                            historyMessages.add(chatMessage);
                        }
                    }

                    adapter.setMessages(historyMessages);
                    recyclerViewMessages.scrollToPosition(adapter.getItemCount() - 1);
                    isHistoryLoaded = true;
                    android.util.Log.d(TAG, "✅ Historial cargado: " + messagesArray.length() + " mensajes");
                    android.util.Log.d(TAG, "╚═══════════════════════════════════════════╝");
                });
            } catch (Exception e) {
                android.util.Log.e(TAG, "❌ Error al cargar historial: " + e.getMessage());
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    isHistoryLoaded = true;
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatId != null && !chatId.isEmpty() && !isHistoryLoaded) {
            loadChatHistory();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        android.util.Log.d(TAG, "👋 onDestroyView");
        if (wsClient != null) {
            wsClient.getReceivedMessages().removeObservers(getViewLifecycleOwner());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d(TAG, "💥 onDestroy: Limpiando datos");
        shownMessageIds.clear();
        isHistoryLoaded = false;
        isViewCreated = false;
        if (wsClient != null) {
            wsClient.disconnect();
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
}