package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class CitaResumenData {
    @SerializedName("id_cita")
    private int idCita;
    @SerializedName("codigo_qr")
    private String codigoQr;
    @SerializedName("estado")
    private String estado;
    @SerializedName("fecha")
    private String hora;
    @SerializedName("hora")
    private String fecha;

    public int getIdCita() {
        return idCita;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public String getEstado() {
        return estado;
    }

    public String getHora() {
        return hora;
    }

    public String getFecha() {
        return fecha;
    }
}
