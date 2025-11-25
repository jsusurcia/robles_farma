package com.example.robles_farma.response

import com.example.robles_farma.model.EspecialidadData

data class EspecialidadResponse (
    val status: String,
    val message: String,
    val data: List<EspecialidadData>
)
