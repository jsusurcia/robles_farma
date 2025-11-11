package com.example.robles_farma.model;

public class ChatMessage {
    private String id;
    private String text;
    private String senderId;
    private String senderName;
    private String timestamp;
    private boolean isSentByMe;

    public ChatMessage(String id, String text, String senderId, String senderName, String timestamp, boolean isSentByMe) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.isSentByMe = isSentByMe;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSentByMe() {
        return isSentByMe;
    }
}