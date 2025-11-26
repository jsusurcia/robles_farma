package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;

public class ReservaRequest {
    @SerializedName("id_horario")
    private int idHorario;
    @SerializedName("direccion_domicilio")
    private String direccionDomicilio;
    @SerializedName("id_tipo_pago")
    private int idTipoPago;

    public ReservaRequest(int idHorario, String direccionDomicilio, int idTipoPago) {
        this.idHorario = idHorario;
        this.direccionDomicilio = direccionDomicilio;
        this.idTipoPago = idTipoPago;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public String getDireccionDomicilio() {
        return direccionDomicilio;
    }

    public void setDireccionDomicilio(String direccionDomicilio) {
        this.direccionDomicilio = direccionDomicilio;
    }

    public int getIdTipoPago() {
        return idTipoPago;
    }

    public void setIdTipoPago(int idTipoPago) {
        this.idTipoPago = idTipoPago;
    }
}
