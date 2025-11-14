package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class PersonalMedicoResponse {

    @SerializedName("id")
    public int id;

    @SerializedName("nombres")
    public String nombres;

    @SerializedName("apellido_paterno")
    public String apellidoPaterno;

    @SerializedName("apellido_materno")
    public String apellidoMaterno;

    @SerializedName("correo")
    public String correo;

    public String getNombreCompleto() {
        return nombres + " " + apellidoPaterno + " " + apellidoMaterno;
    }

}
