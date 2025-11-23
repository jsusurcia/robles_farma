package com.example.robles_farma.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robles_farma.R;
import com.example.robles_farma.adapter.ChatListAdapter;
import com.example.robles_farma.response.ChatResponse;
import com.example.robles_farma.response.MessageResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    private static final String TAG = "ChatListFragment";

    // Constantes para navegación
    private static final String ARG_CHAT_ID = "chatId";
    private static final String ARG_DOC_NAME = "doctorName";
    private static final String ARG_DOC_ID = "doctorId";
    private static final String ROL_PACIENTE = "paciente";

    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    private final List<ChatItem> chatList = new ArrayList<>();
    private final Map<String, ChatItem> chatMap = new HashMap<>();

    private boolean isLoading = false;
    private final ApiService apiService = RetrofitClient.createService();

    public ChatListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupAdapter(view);

        // Cargar chats si la lista está vacía al crear la vista
        if (!isLoading && chatList.isEmpty()) {
            loadChats();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refrescar al volver para ver últimos mensajes actualizados
        loadChats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isLoading = false;
        // No limpiamos chatMap aquí si queremos persistencia al rotar,
        // pero si prefieres recargar siempre, puedes descomentar:
        // chatMap.clear();
    }

    private void setupAdapter(View view) {
        adapter = new ChatListAdapter(chatList, chat -> {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_CHAT_ID, chat.getId());
            bundle.putString(ARG_DOC_NAME, chat.getName());
            bundle.putString(ARG_DOC_ID, chat.getDoctorId());

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_navigation_chat_to_chat_detail, bundle);
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadChats() {
        if (isLoading) return;

        String token = LoginStorage.getToken(requireContext());
        String currentUserId = LoginStorage.getUserId(requireContext());

        if (token == null) {
            Toast.makeText(requireContext(), "Sesión expirada. Inicia sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        isLoading = true;
        String authToken = "Bearer " + token;

        apiService.getChats(authToken).enqueue(new Callback<List<ChatResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatResponse>> call, @NonNull Response<List<ChatResponse>> response) {
                isLoading = false;

                if (!response.isSuccessful() || response.body() == null) return;

                // Limpiamos para evitar duplicados al recargar
                chatList.clear();
                chatMap.clear();

                for (ChatResponse chat : response.body()) {
                    processSingleChat(chat, currentUserId);
                }

                refreshListUI();
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatResponse>> call, @NonNull Throwable t) {
                isLoading = false;
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al cargar chats", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "Error API Chats: " + t.getMessage());
            }
        });
    }

    private void processSingleChat(ChatResponse chat, String currentUserId) {
        String chatId = chat.getChatId();
        String otherUserId = "";
        String myRole = "";

        // Identificar roles
        for (ChatResponse.Participant p : chat.getParticipants()) {
            if (Objects.equals(p.getUserId(), currentUserId)) {
                myRole = p.getRol();
            } else {
                otherUserId = p.getUserId();
            }
        }

        // Determinar nombre a mostrar
        String displayName;
        if (ROL_PACIENTE.equalsIgnoreCase(myRole)) {
            displayName = chat.getPersonalMedicoNombre();
        } else {
            displayName = chat.getPacienteNombre();
        }

        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = "Usuario Desconocido";
        }

        // Crear item inicial
        ChatItem item = new ChatItem(
                chatId,
                displayName,
                "Cargando...",
                "",
                false,
                otherUserId
        );

        chatMap.put(chatId, item);

        // Cargar último mensaje
        loadLastMessage(chatId);
    }

    private void loadLastMessage(String chatId) {
        if (getContext() == null) return;

        String token = LoginStorage.getToken(requireContext());
        String authToken = "Bearer " + token;

        apiService.getChatMessages(chatId, authToken).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageResponse>> call, @NonNull Response<List<MessageResponse>> response) {
                ChatItem item = chatMap.get(chatId);
                if (item == null) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    MessageResponse last = response.body().get(response.body().size() - 1);
                    item.setLastMessage(last.getText());
                    item.setTimestamp(last.getTimestamp());
                } else {
                    item.setLastMessage("Sin mensajes aún");
                    item.setTimestamp("");
                }

                refreshListUI();
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error msg chat " + chatId + ": " + t.getMessage());
            }
        });
    }

    private void refreshListUI() {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            chatList.clear();
            chatList.addAll(chatMap.values());
            // Idealmente usar DiffUtil en lugar de notifyDataSetChanged para mejor rendimiento
            adapter.notifyDataSetChanged();
        });
    }
}