package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class ComprobanteData {
    @SerializedName("nro_comprobante")
    private String nroComprobante;
    @SerializedName("fecha_emision")
    private String fechaEmision;
    @SerializedName("monto_total")
    private Float montoTotal;
    @SerializedName("igv")
    private Float igv;

    public String getNroComprobante() {
        return nroComprobante;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public Float getMontoTotal() {
        return montoTotal;
    }

    public Float getIgv() {
        return igv;
    }
}
