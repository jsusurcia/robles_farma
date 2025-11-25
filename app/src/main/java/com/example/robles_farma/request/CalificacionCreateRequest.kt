package com.example.robles_farma.request

import com.google.gson.annotations.SerializedName

data class CalificacionCreateRequest(
    @SerializedName("id_cita")
    val idCita: Int,

    val puntuacion: Int,

    val comentario: String? = null
)
