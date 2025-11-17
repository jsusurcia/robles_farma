package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;

public class EditarUbicacionCitaRequest {
    @SerializedName("direccion_domicilio")
    private String direccionDomicilio;

    public EditarUbicacionCitaRequest(String direccionDomicilio) {
        this.direccionDomicilio = direccionDomicilio;
    }

    public String getDireccionDomicilio() {
        return direccionDomicilio;
    }

    public void setDireccionDomicilio(String direccionDomicilio) {
        this.direccionDomicilio = direccionDomicilio;
    }
}
