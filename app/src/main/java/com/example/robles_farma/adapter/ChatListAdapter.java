package com.example.robles_farma.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robles_farma.R;
import com.example.robles_farma.response.FotoUploadResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.ui.chat.ChatItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.bumptech.glide.Glide;

import android.util.Log;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private final List<ChatItem> chatList;
    private final OnChatClickListener listener;

    // 👇 Interfaz para manejar clics en los chats
    public interface OnChatClickListener {
        void onChatClick(ChatItem chat);
    }

    public ChatListAdapter(List<ChatItem> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_doctor, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem chat = chatList.get(position);
        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDoctor;
        TextView tvDoctorName, tvLastMessage, tvTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDoctor = itemView.findViewById(R.id.imgDoctor);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

        }

        public void bind(ChatItem chat, OnChatClickListener listener) {
            tvDoctorName.setText(chat.getName());
            tvLastMessage.setText(chat.getLastMessage());

            // 👇 NUEVO: Mostrar timestamp si existe
            if (tvTimestamp != null) {
                String timestamp = chat.getTimestamp();
                if (timestamp != null && !timestamp.isEmpty()) {
                    tvTimestamp.setText(timestamp);
                    tvTimestamp.setVisibility(View.VISIBLE);
                } else {
                    tvTimestamp.setVisibility(View.GONE);
                }
            }

            cargarFotoDoctor(chat.getDoctorId());

            itemView.setOnClickListener(v -> listener.onChatClick(chat));
        }

        private void cargarFotoDoctor(String doctorIdStr) {
            // Reseteamos la imagen para evitar que se mezclen al scrollear (reciclaje de vistas)
            imgDoctor.setImageResource(android.R.drawable.ic_menu_myplaces);

            if (doctorIdStr == null || doctorIdStr.isEmpty()) return;

            try {
                int doctorId = Integer.parseInt(doctorIdStr);

                // Instanciamos el servicio (Retrofit maneja el pool de conexiones, es ligero instanciarlo aquí)
                ApiService apiService = RetrofitClient.createService();

                // Llamada a tu endpoint
                apiService.getFotoPersonal(doctorId).enqueue(new Callback<FotoUploadResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FotoUploadResponse> call, @NonNull Response<FotoUploadResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String url = response.body().getUrl();

                            // Validamos que la URL y el Contexto existan antes de pintar
                            if (url != null && !url.isEmpty() && itemView.getContext() != null) {
                                Glide.with(itemView.getContext())
                                        .load(url)
                                        .placeholder(android.R.drawable.ic_menu_myplaces)
                                        .error(android.R.drawable.ic_menu_myplaces)
                                        .circleCrop() // Hace la imagen redonda
                                        .into(imgDoctor);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<FotoUploadResponse> call, @NonNull Throwable t) {
                        // Si falla, dejamos el icono por defecto que pusimos al inicio
                        Log.e("ChatAdapter", "Error cargando imagen: " + t.getMessage());
                    }
                });

            } catch (NumberFormatException e) {
                Log.e("ChatAdapter", "ID de doctor inválido: " + doctorIdStr);
            }
        }
    }
}