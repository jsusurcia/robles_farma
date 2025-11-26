package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class TipoPagoResponse {
    @SerializedName("id_tipo_pago")
    private int idTipoPago;
    @SerializedName("nombre_tipo_pago")
    private String nombreTipoPago;

    public TipoPagoResponse(int idTipoPago, String nombreTipoPago) {
        this.idTipoPago = idTipoPago;
        this.nombreTipoPago = nombreTipoPago;
    }

    public int getIdTipoPago() {
        return idTipoPago;
    }

    public String getNombreTipoPago() {
        return nombreTipoPago;
    }
}
