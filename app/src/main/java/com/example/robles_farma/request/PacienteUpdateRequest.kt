package com.example.robles_farma.request

import com.google.gson.annotations.SerializedName

data class PacienteUpdateRequest(
    @SerializedName("nro_documento")
    val nroDocumento: String? = null,

    @SerializedName("nombres")
    val nombres: String? = null,

    @SerializedName("apellido_paterno")
    val apellidoPaterno: String? = null,

    @SerializedName("apellido_materno")
    val apellidoMaterno: String? = null,

    @SerializedName("fecha_nacimiento")
    val fechaNacimiento: String? = null,

    @SerializedName("sexo")
    val sexo: Boolean? = null,

    @SerializedName("estado")
    val estado: Boolean? = null,

    @SerializedName("foto_perfil_url")
    val fotoPerfilUrl: String? = null,

    @SerializedName("contacto_emergencia_nombre")
    val contactoEmergenciaNombre: String? = null,

    @SerializedName("contacto_emergencia_telefono")
    val contactoEmergenciaTelefono: String? = null
)
