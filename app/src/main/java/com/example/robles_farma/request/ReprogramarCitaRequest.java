package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;

public class ReprogramarCitaRequest {
    @SerializedName("id_horario")
    private int idHorario;

    public ReprogramarCitaRequest(int idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }
}
