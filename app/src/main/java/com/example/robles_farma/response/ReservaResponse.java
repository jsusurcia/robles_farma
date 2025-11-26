package com.example.robles_farma.response;

import com.example.robles_farma.model.ReservaData;
import com.google.gson.annotations.SerializedName;

public class ReservaResponse {
    private String status;
    private String message;
    private ReservaData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReservaData getData() {
        return data;
    }

    public void setData(ReservaData data) {
        this.data = data;
    }
}
