package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class TipoDocumentoResponse {

    @SerializedName("id_tipo_documento")
    private int idTipoDocumento;

    @SerializedName("tipo_documento")
    private String tipoDocumento;

    // Constructor vacío
    public TipoDocumentoResponse() {
    }

    // Constructor con parámetros
    public TipoDocumentoResponse(int idTipoDocumento, String tipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
        this.tipoDocumento = tipoDocumento;
    }

    // Getters y Setters
    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @Override
    public String toString() {
        return "TipoDocumentoResponse{" +
                "idTipoDocumento=" + idTipoDocumento +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                '}';
    }
}