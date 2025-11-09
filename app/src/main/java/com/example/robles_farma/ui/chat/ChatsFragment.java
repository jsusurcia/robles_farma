package com.example.robles_farma.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.robles_farma.R;
import com.example.robles_farma.sharedpreferences.LoginStorage;
import com.example.robles_farma.websocket.ChatWebSocketClient;

import java.util.Arrays;

public class ChatsFragment extends Fragment {

    private ChatWebSocketClient wsClient;
    private EditText etMessage;
    private Button btnSend;
    private TextView tvMessages;

    public ChatsFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflar layout XML (lo crearemos en el siguiente paso)
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        tvMessages = view.findViewById(R.id.tvMessages);

        // Inicializa el WebSocket
        wsClient = new ChatWebSocketClient();

        // Recupera tu JWT
        String token = LoginStorage.getToken(getContext());
        wsClient.connect(token);

        // Escucha mensajes entrantes
        wsClient.receivedMessages.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                Log.d("ChatsFragment", "Nuevo mensaje: " + message);
                tvMessages.append("" + message + "\n");
            }
        });

        // Envía mensajes
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                wsClient.sendMessage(message, "69f0ff160b880b331e43513fb", Arrays.asList("1", "3"));
                tvMessages.append("" + message + "\n");
                etMessage.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wsClient.disconnect();
    }
}
