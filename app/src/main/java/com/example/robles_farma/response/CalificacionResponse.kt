package com.example.robles_farma.response

import com.example.robles_farma.model.CalificacionData

data class CalificacionResponse (
    val status: String,
    val message: String,
    val data: CalificacionData?
)