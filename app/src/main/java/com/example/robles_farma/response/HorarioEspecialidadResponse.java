package com.example.robles_farma.response;

import com.example.robles_farma.model.HorarioEspecialidadData;

public class HorarioEspecialidadResponse {
    private String status;
    private String message;
    private HorarioEspecialidadData[] data;

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

    public HorarioEspecialidadData[] getData() {
        return data;
    }

    public void setData(HorarioEspecialidadData[] data) {
        this.data = data;
    }
}
