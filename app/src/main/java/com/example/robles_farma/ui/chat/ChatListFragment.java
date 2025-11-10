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

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_navigation_chat_to_chat_detail, bundle);
        });

        recyclerView.setAdapter(adapter);

        // Cargar los chats desde el backend
        loadChats();
    }

    private void loadChats() {
        ChatService chatService = RetrofitClient.createService(requireContext(), ChatService.class);
        String currentUserId = LoginStorage.getUserId(requireContext()); // 👈 Tu método que devuelve el ID del usuario logueado

        chatService.getChats().enqueue(new Callback<List<ChatResponse>>() {
            @Override
            public void onResponse(Call<List<ChatResponse>> call, Response<List<ChatResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chatList.clear();

                    for (ChatResponse chat : response.body()) {
                        boolean belongsToUser = false;
                        String otherName = "Desconocido";

                        for (ChatResponse.Participant participant : chat.getParticipants()) {
                            if (participant.getUserId().equals(currentUserId)) {
                                belongsToUser = true;
                            }
                        }

                        if (belongsToUser) {
                            // Buscar al otro participante para mostrarlo
                            for (ChatResponse.Participant participant : chat.getParticipants()) {
                                if (!participant.getUserId().equals(currentUserId)) {
                                    if ("personal_medico".equalsIgnoreCase(participant.getRol())) {
                                        otherName = "Dr. ID: " + participant.getUserId();
                                    } else if ("paciente".equalsIgnoreCase(participant.getRol())) {
                                        otherName = "Paciente ID: " + participant.getUserId();
                                    }
                                }
                            }

                            chatList.add(new ChatItem(
                                    chat.getChatId(),
                                    otherName,
                                    "Sin mensajes aún 💬"
                            ));
                        }
                    }

                    adapter.notifyDataSetChanged();
                    Log.i("ChatListFragment", "✅ Chats cargados correctamente: " + chatList.size());
                } else {
                    Log.e("ChatListFragment", "❌ Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatResponse>> call, Throwable t) {
                Log.e("ChatListFragment", "⚠️ Error cargando chats: " + t.getMessage());
            }
        });
    }
}
