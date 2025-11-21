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
import com.example.robles_farma.retrofit.ChatService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    // ya no necesitamos el mapa estático de nombres
    private static final String TAG = "ChatListFragment";
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private final List<ChatItem> chatList = new ArrayList<>();
    // Usamos un mapa para actualizar mensajes rápidamente sin duplicar chats en la lista visual
    private final Map<String, ChatItem> chatMap = new HashMap<>();
    private boolean isLoading = false;

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

        // Configurar el adaptador
        adapter = new ChatListAdapter(chatList, chat -> {
            Bundle bundle = new Bundle();
            bundle.putString("chatId", chat.getId());
            bundle.putString("doctorName", chat.getName()); // Puede ser nombre de doctor o paciente según el rol
            bundle.putString("doctorId", chat.getDoctorId()); // ID del otro participante

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_navigation_chat_to_chat_detail, bundle);
        });

        recyclerView.setAdapter(adapter);

        // Cargar chats si la lista está vacía
        if (!isLoading && chatList.isEmpty()) {
            loadChats();
        }
    }

    private void loadChats() {
        if (isLoading) return;

        String token = LoginStorage.getToken(requireContext());
        String currentUserId = LoginStorage.getUserId(requireContext());

        if (token == null) {
            Toast.makeText(requireContext(), "Sesión expirada. Inicia sesión.", Toast.LENGTH_LONG).show();
            return;
        }

        RetrofitClient.API_TOKEN = token;
        isLoading = true;

        ChatService chatService = RetrofitClient.createService(requireContext(), ChatService.class);

        chatService.getChats().enqueue(new Callback<List<ChatResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatResponse>> call, @NonNull Response<List<ChatResponse>> response) {
                isLoading = false;

                if (!response.isSuccessful() || response.body() == null) return;

                chatList.clear();
                chatMap.clear();

                for (ChatResponse chat : response.body()) {
                    String chatId = chat.getChatId();

                    // 1. Identificar al "otro" participante para guardar su ID
                    String otherUserId = "";
                    String myRole = ""; // Para saber si soy paciente o médico

                    for (ChatResponse.Participant p : chat.getParticipants()) {
                        if (p.getUserId().equals(currentUserId)) {
                            myRole = p.getRol(); // Averiguo mi rol en este chat
                        } else {
                            otherUserId = p.getUserId(); // Este es el ID de la otra persona
                        }
                    }

                    // 2. Decidir qué nombre mostrar basado en MI rol
                    String displayName;
                    if ("paciente".equalsIgnoreCase(myRole)) {
                        // Si yo soy paciente, muestro el nombre del médico
                        displayName = chat.getPersonalMedicoNombre();
                    } else {
                        // Si yo soy médico (o cualquier otro), muestro el nombre del paciente
                        displayName = chat.getPacienteNombre();
                    }

                    // Fallback por si viene nulo
                    if (displayName == null || displayName.trim().isEmpty()) {
                        displayName = "Usuario Desconocido";
                    }

                    // 3. Crear el objeto para la lista
                    ChatItem item = new ChatItem(
                            chatId,
                            displayName,
                            "Cargando...",           // Placeholder mensaje
                            "",                      // Placeholder hora
                            false,
                            otherUserId
                    );

                    chatMap.put(chatId, item);

                    // 4. Cargar el último mensaje individualmente
                    loadLastMessage(chatId);
                }

                refreshList();
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatResponse>> call, @NonNull Throwable t) {
                isLoading = false;
                Toast.makeText(requireContext(), "Error al cargar chats", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error API Chats: " + t.getMessage());
            }
        });
    }


    private void loadLastMessage(String chatId) {
        ChatService chatService = RetrofitClient.createService(requireContext(), ChatService.class);

        chatService.getChatMessages(chatId).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MessageResponse>> call, @NonNull Response<List<MessageResponse>> response) {
                ChatItem item = chatMap.get(chatId);
                if (item == null) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // El backend devuelve ordenado por timestamp, el último es el más reciente
                    MessageResponse last = response.body().get(response.body().size() - 1);
                    item.setLastMessage(last.getText());
                    item.setTimestamp(last.getTimestamp()); // Asegúrate de formatear esto si es necesario
                } else {
                    item.setLastMessage("Sin mensajes aún");
                    item.setTimestamp("");
                }

                refreshList();
            }

            @Override
            public void onFailure(@NonNull Call<List<MessageResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error cargando mensajes para chat " + chatId + ": " + t.getMessage());
            }
        });
    }

    private void refreshList() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            chatList.clear();
            chatList.addAll(chatMap.values());
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar al volver para actualizar últimos mensajes
        if (!isLoading) {
            loadChats();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpiar para evitar memory leaks o referencias viejas
        chatMap.clear();
        isLoading = false;
    }
}