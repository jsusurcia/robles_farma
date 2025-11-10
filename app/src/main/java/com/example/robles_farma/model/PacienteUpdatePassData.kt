package com.example.robles_farma.model

import com.google.gson.annotations.SerializedName

data class PacienteUpdatePassData (
    @SerializedName("id_paciente")
    val idPaciente: Int,

    @SerializedName("nombres")
    val nombres: String,

    @SerializedName("apellido_paterno")
    val apellidoPaterno: String,

    @SerializedName("apellido_materno")
    val apellidoMaterno: String
)