package com.example.robles_farma.model

import com.google.gson.annotations.SerializedName

data class CalificacionData (
    @SerializedName("id_calificacion")
    val idCalificacion: Int,
    @SerializedName("id_cita")
    val idCita: Int,
    val puntuacion: Int
)