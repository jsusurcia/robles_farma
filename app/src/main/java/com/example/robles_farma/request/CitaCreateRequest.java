package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;

public class CitaCreateRequest {

    @SerializedName("id_horario")
    private int idHorario;

    @SerializedName("direccion_domicilio")
    private String direccionDomicilio;

    public CitaCreateRequest(int idHorario, String direccionDomicilio) {
        this.idHorario = idHorario;
        this.direccionDomicilio = direccionDomicilio;
    }

    // Getters y Setters (en camelCase)
    public int getIdHorario() { return idHorario; }
    public void setIdHorario(int idHorario) { this.idHorario = idHorario; }

    public String getDireccionDomicilio() { return direccionDomicilio; }
    public void setDireccionDomicilio(String direccionDomicilio) { this.direccionDomicilio = direccionDomicilio; }
}