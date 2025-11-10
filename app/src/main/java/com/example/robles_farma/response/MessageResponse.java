package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("text")
    private String text;

    @SerializedName("sender_id")
    private String senderId;

    @SerializedName("receiver_id")
    private String receiverId;

    @SerializedName("created_at")
    private String createdAt;

    // 🔹 Constructor
    public MessageResponse(String id, String text, String senderId, String receiverId, String createdAt) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.createdAt = createdAt;
    }

    // 🔹 Getters
    public String getId() { return id; }
    public String getText() { return text; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getCreatedAt() { return createdAt; }
}
