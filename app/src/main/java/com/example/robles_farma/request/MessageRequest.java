package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MessageRequest {

    @SerializedName("text")
    private String text;

    @SerializedName("chat_id")
    private String chatId;

    @SerializedName("recipient_ids")
    private List<String> recipientIds;

    public MessageRequest(String text, String chatId, List<String> recipientIds) {
        this.text = text;
        this.chatId = chatId;
        this.recipientIds = recipientIds;
    }

    public String getText() { return text; }
    public String getChatId() { return chatId; }
    public List<String> getRecipientIds() { return recipientIds; }
}
