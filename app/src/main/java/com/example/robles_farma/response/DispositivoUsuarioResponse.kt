package com.example.robles_farma.response

import com.google.gson.annotations.SerializedName

data class DispositivoUsuarioResponse (
    @SerializedName("id")
    val id: Int,
    @SerializedName("fcm_token")
    val fcmToken: String,
    @SerializedName("plataforma")
    val plataforma: String,
    @SerializedName("id_paciente")
    val idPaciente: Int
)