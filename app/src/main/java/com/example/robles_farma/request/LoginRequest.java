package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("nro_documento")
    private String nroDocumento;
    @SerializedName("clave")
    private String clave;

    public LoginRequest(String nroDocumento, String clave) {
        this.nroDocumento = nroDocumento;
        this.clave = clave;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
