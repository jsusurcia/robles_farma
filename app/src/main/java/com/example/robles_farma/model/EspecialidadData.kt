package com.example.robles_farma.model

import com.google.gson.annotations.SerializedName

data class EspecialidadData (
    @SerializedName("id_especialidad")
    val idEspecialidad: Int,
    val nombre: String,
    @SerializedName("icono_url")
    val iconoUrl: String,
    @SerializedName("total_citas")
    val totalCitas: Int
)
