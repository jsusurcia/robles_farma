package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class PacienteAseguradoResponse {
    @SerializedName("es_asegurado")
    private boolean esAsegurado;

    public PacienteAseguradoResponse(boolean esAsegurado) {
        this.esAsegurado = esAsegurado;
    }

    public boolean isEsAsegurado() {
        return esAsegurado;
    }

    public void setEsAsegurado(boolean esAsegurado) {
        this.esAsegurado = esAsegurado;
    }
}
