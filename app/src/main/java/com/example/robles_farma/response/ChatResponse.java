package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatResponse {

    @SerializedName("chat_id")
    private String chatId;

    @SerializedName("participants")
    private List<Participant> participants;

    @SerializedName("personal_medico_nombre")
    private String personalMedicoNombre;

    @SerializedName("paciente_nombre")
    private String pacienteNombre;

    @SerializedName("created_at")
    private String createdAt;

    public String getChatId() {
        return chatId;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String getPersonalMedicoNombre() {
        return personalMedicoNombre;
    }

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static class Participant {

        @SerializedName("user_id")
        private String userId;

        @SerializedName("rol")
        private String rol;

        public String getUserId() {
            return userId;
        }

        public String getRol() {
            return rol;
        }
    }
}