package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatResponse {

    @SerializedName("chat_id")
    private String chatId;

    private List<Participant> participants;

    @SerializedName("created_at")
    private String createdAt;

    // Estos campos extras están OK, pero RECUERDA:
    // No vienen desde el backend, los llenas tú si quieres usarlos
    private String userName;
    private String doctorName;
    private String doctorId;

    public String getChatId() {
        return chatId;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // GETTERS nuevos
    public String getUserName() {
        return userName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    // SETTERS nuevos
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    // PARTICIPANT — CORREGIDO ✔
    public static class Participant {

        @SerializedName("user_id")
        private String user_id;

        @SerializedName("rol")
        private String rol;

        @SerializedName("nombre")   // <-- ESTE ES EL CAMPO QUE FALTABA
        private String nombre;

        public String getUserId() {
            return user_id;
        }

        public String getRol() {
            return rol;
        }

        public String getNombre() {   // <-- GETTER DEL NOMBRE
            return nombre;
        }
    }
}
