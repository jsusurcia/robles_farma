package com.example.robles_farma.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.robles_farma.R;
import com.example.robles_farma.sharedpreferences.LoginStorage;
import com.example.robles_farma.websocket.ChatWebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class ChatsFragment extends Fragment {

    private ChatWebSocketClient wsClient;
    private EditText etMessage;
    private Button btnSend;
    private TextView tvMessages;

    private String doctorId;
    private String doctorName;

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

        // 🔹 Vincula todos los elementos del layout
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        tvMessages = view.findViewById(R.id.tvMessages);


        // 🔹 Recupera los datos del doctor
        if (getArguments() != null) {
            doctorId = getArguments().getString("doctorId");
            doctorName = getArguments().getString("doctorName");
        }

        // 🔹 Inicializa y conecta el WebSocket
        wsClient = new ChatWebSocketClient();
        String token = LoginStorage.getToken(getContext());
        wsClient.connect(token);

        // 🔹 Observa los mensajes recibidos
        wsClient.getReceivedMessages().observe(getViewLifecycleOwner(), raw -> {
            try {
                JSONObject obj = new JSONObject(raw);
                String sender = obj.optString("sender_rol", "otro");
                String text = obj.optString("text", raw);
                tvMessages.append(sender + ": " + text + "\n");
            } catch (JSONException e) {
                tvMessages.append("📩 " + raw + "\n");
            }
        });

        // 🔹 Enviar mensaje
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                wsClient.sendMessage(message, "69ff160b88bb331e43513fb", Arrays.asList("1", "3"));
                tvMessages.append("Tú: " + message + "\n");
                etMessage.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (wsClient != null) {
            wsClient.disconnect();
        }
    }
}
