package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class CitaResponse {

    @SerializedName("id_cita")
    private int idCita;

    @SerializedName("estado_cita")
    private String estadoCita;

    @SerializedName("codigo_qr")
    private String codigoQr;

    @SerializedName("fecha_creacion")
    private String fechaCreacion;

    // Getters en camelCase
    public int getIdCita() { return idCita; }
    public String getEstadoCita() { return estadoCita; }
    public String getCodigoQr() { return codigoQr; }
    public String getFechaCreacion() { return fechaCreacion; }
}