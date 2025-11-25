package com.example.robles_farma.response;

import com.example.robles_farma.model.CitasData;

public class ReprogramarCitaResponse {
    private String status;
    private String message;
    private CitasData data;

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

    public CitasData getData() {
        return data;
    }

    public void setData(CitasData data) {
        this.data = data;
    }
}
