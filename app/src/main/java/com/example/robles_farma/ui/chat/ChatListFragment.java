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
    private final List<ChatItem> chatList = new ArrayList<>();

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

        adapter = new ChatListAdapter(chatList, chat -> {
            Bundle bundle = new Bundle();
            bundle.putString("chatId", chat.getId());
            bundle.putString("doctorName", chat.getName());
            bundle.putString("doctorId", extractIdFromName(chat.getName()));

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_navigation_chat_to_chat_detail, bundle);
        });

        recyclerView.setAdapter(adapter);
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

                        for (ChatResponse.Participant participant : chat.getParticipants()) {
                            if (participant.getUserId().equals(currentUserId)) {
                                belongsToUser = true;
                                break;
                            }
                        }

                        if (belongsToUser) {
                            for (ChatResponse.Participant participant : chat.getParticipants()) {
                                if (!participant.getUserId().equals(currentUserId)) {
                                    otherUserId = participant.getUserId();
                                    otherRol = participant.getRol();

                                    if ("personal_medico".equalsIgnoreCase(otherRol)) {
                                        otherName = "Dr. (ID: " + otherUserId + ")";
                                    } else if ("paciente".equalsIgnoreCase(otherRol)) {
                                        otherName = "Paciente (ID: " + otherUserId + ")";
                                    }
                                    break;
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
                    Log.i(TAG, "Chats cargados: " + chatList.size());
                } else {
                    Log.e(TAG, "Error al obtener chats: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatResponse>> call, Throwable t) {
                Log.e(TAG, "Error cargando chats: " + t.getMessage());
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
                return name.substring(start, end).trim();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extrayendo ID: " + e.getMessage());
        }
        return "";
    }
}
