package com.example.robles_farma.response

import com.example.robles_farma.model.BusquedaEspecialidadData

data class BusquedaEspecialidadResponse (
    val status: String,
    val message: String,
    val data: List<BusquedaEspecialidadData>
)
