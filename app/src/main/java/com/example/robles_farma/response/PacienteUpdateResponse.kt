package com.example.robles_farma.response

import com.example.robles_farma.model.PacienteUpdateData

data class PacienteUpdateResponse (
    val status: String,
    val message: String,
    val data: PacienteUpdateData
)