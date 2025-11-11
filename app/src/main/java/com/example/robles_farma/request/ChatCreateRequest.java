package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;

public class ChatCreateRequest {
    @SerializedName("recipient_id")
    private String recipientId;

    public ChatCreateRequest(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientId() {
        return recipientId;
    }
}
