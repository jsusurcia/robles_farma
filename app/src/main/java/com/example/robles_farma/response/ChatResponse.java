package com.example.robles_farma.response;

import java.util.List;

public class ChatResponse {
    private String chat_id;
    private List<Participant> participants;
    private String created_at;

    public String getChatId() {
        return chat_id;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String getCreatedAt() {
        return created_at;
    }

    // Clase interna para los participantes
    public static class Participant {
        private String user_id;
        private String rol;

        public String getUserId() {
            return user_id;
        }

        public String getRol() {
            return rol;
        }
    }
}
