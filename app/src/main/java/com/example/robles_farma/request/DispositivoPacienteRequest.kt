package com.example.robles_farma.request

import com.google.gson.annotations.SerializedName

data class DispositivoPacienteRequest (
    @SerializedName("id_paciente")
    val idPaciente: Int,
    @SerializedName("fcm_token")
    val fcmToken: String,
    val plataforma: String
)