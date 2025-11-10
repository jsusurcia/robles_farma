package com.example.robles_farma.response

import com.example.robles_farma.model.PacienteUpdatePassData

class PacienteUpdatePassResponse (
    val status: String,
    val message: String,
    val data: PacienteUpdatePassData
)