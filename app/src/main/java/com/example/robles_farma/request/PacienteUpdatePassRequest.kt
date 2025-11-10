package com.example.robles_farma.request

import com.google.gson.annotations.SerializedName

data class PacienteUpdatePassRequest (
    @SerializedName("clave_actual")
    val claveActual: String,

    @SerializedName("clave_nueva")
    val claveNueva: String
)