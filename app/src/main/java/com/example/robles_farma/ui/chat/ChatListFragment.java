package com.example.robles_farma.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.robles_farma.retrofit.ChatService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    private static final String TAG = "ChatListFragment";
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private List<ChatItem> chatList = new ArrayList<>();

    public ChatListFragment() {
        // Constructor vacío
    }

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

        // Inicializar adapter vacío
        adapter = new ChatListAdapter(chatList, chat -> {
            Bundle bundle = new Bundle();
            bundle.putString("chatId", chat.getId());
            bundle.putString("doctorName", chat.getName());

            // 🔥 CRÍTICO: Extraer y pasar doctorId correctamente
            // El nombre viene como "Dr. ID: 123" o "Paciente ID: 456"
            String doctorId = extractIdFromName(chat.getName());
            bundle.putString("doctorId", doctorId);

            Log.d(TAG, "📤 Navegando al chat:");
            Log.d(TAG, "  - chatId: " + chat.getId());
            Log.d(TAG, "  - doctorId: " + doctorId);
            Log.d(TAG, "  - doctorName: " + chat.getName());

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_navigation_chat_to_chat_detail, bundle);
        });

        recyclerView.setAdapter(adapter);

        // Cargar los chats desde el backend
        loadChats();
    }

    private void loadChats() {
        ChatService chatService = RetrofitClient.createService(requireContext(), ChatService.class);
        String currentUserId = LoginStorage.getUserId(requireContext());

        chatService.getChats().enqueue(new Callback<List<ChatResponse>>() {
            @Override
            public void onResponse(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatList.clear();

                    for (ChatResponse chat : response.body()) {
                        boolean belongsToUser = false;
                        String otherUserId = "";
                        String otherName = "Desconocido";
                        String otherRol = "";

                        // Verificar si pertenezco al chat
                        for (ChatResponse.Participant participant : chat.getParticipants()) {
                            if (participant.getUserId().equals(currentUserId)) {
                                belongsToUser = true;
                                break;
                            }
                        }

                        if (belongsToUser) {
                            // 🔥 Buscar al OTRO participante
                            for (ChatResponse.Participant participant : chat.getParticipants()) {
                                if (!participant.getUserId().equals(currentUserId)) {
                                    otherUserId = participant.getUserId();
                                    otherRol = participant.getRol();

                                    // Construir nombre descriptivo
                                    if ("personal_medico".equalsIgnoreCase(otherRol)) {
                                        otherName = "Dr. (ID: " + otherUserId + ")";
                                    } else if ("paciente".equalsIgnoreCase(otherRol)) {
                                        otherName = "Paciente (ID: " + otherUserId + ")";
                                    }
                                    break;
                                }
                            }

                            // 🔥 CREAR ChatItem con ID guardado en el nombre (temporal)
                            // O mejor aún: modifica ChatItem para tener un campo doctorId
                            chatList.add(new ChatItem(
                                    chat.getChatId(),
                                    otherName,
                                    "Sin mensajes aún 💬"
                            ));

                            Log.d(TAG, "✅ Chat agregado:");
                            Log.d(TAG, "  - chatId: " + chat.getChatId());
                            Log.d(TAG, "  - otherUserId: " + otherUserId);
                            Log.d(TAG, "  - otherName: " + otherName);
                            Log.d(TAG, "  - otherRol: " + otherRol);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "✅ Chats cargados correctamente: " + chatList.size());
                } else {
                    Log.e(TAG, "❌ Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatResponse>> call, Throwable t) {
                Log.e(TAG, "⚠️ Error cargando chats: " + t.getMessage());
            }
        });
    }

    /**
     * Extrae el ID del nombre que viene en formato "Dr. (ID: 123)" o "Paciente (ID: 456)"
     */
    private String extractIdFromName(String name) {
        try {
            if (name != null && name.contains("(ID: ") && name.contains(")")) {
                int start = name.indexOf("(ID: ") + 5;
                int end = name.indexOf(")", start);
                String id = name.substring(start, end).trim();
                Log.d(TAG, "🔍 ID extraído: '" + id + "' desde nombre: '" + name + "'");
                return id;
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error extrayendo ID del nombre: " + e.getMessage());
        }
        Log.w(TAG, "⚠️ No se pudo extraer ID del nombre: " + name);
        return "";
    }
}