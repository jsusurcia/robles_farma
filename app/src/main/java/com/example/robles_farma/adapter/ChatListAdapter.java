package com.example.robles_farma.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robles_farma.R;
import com.example.robles_farma.ui.chat.ChatItem;

import java.util.List;

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
        TextView tvDoctorName, tvLastMessage, tvTimestamp; // 👈 NUEVO

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgDoctor = itemView.findViewById(R.id.imgDoctor);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp); // 👈 NUEVO
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

            itemView.setOnClickListener(v -> listener.onChatClick(chat));
        }
    }
}