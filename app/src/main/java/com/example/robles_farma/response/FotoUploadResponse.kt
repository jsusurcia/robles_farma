package com.example.robles_farma.response

import com.google.gson.annotations.SerializedName;

data class FotoUploadResponse (
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: String
)