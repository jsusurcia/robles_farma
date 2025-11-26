package com.example.robles_farma.model;

public class ReservaData {
    private String mensaje;
    private CitaResumenData cita;
    private ComprobanteData comprobante;

    public ReservaData(String mensaje, CitaResumenData cita, ComprobanteData comprobante) {
        this.mensaje = mensaje;
        this.cita = cita;
        this.comprobante = comprobante;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public CitaResumenData getCita() {
        return cita;
    }

    public void setCita(CitaResumenData cita) {
        this.cita = cita;
    }

    public ComprobanteData getComprobante() {
        return comprobante;
    }

    public void setComprobante(ComprobanteData comprobante) {
        this.comprobante = comprobante;
    }
}
