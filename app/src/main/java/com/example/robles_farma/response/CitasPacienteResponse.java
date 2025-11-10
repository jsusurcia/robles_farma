package com.example.robles_farma.response;

import com.example.robles_farma.model.CitasData;

public class CitasPacienteResponse {
    private CitasData[] data;
    private String message;
    private String status;

    public CitasData[] getData() {
        return data;
    }

    public void setData(CitasData[] data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
