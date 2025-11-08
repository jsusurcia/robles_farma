package com.example.robles_farma.model

import com.google.gson.annotations.SerializedName

data class BusquedaEspecialidadData (
    @SerializedName("id_especialidad")
    val idEspecialidad: Int,
    val nombre: String
)