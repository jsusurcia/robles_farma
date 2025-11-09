package com.example.robles_farma.model

import com.google.gson.annotations.SerializedName

data class PacienteUpdateData (
    @SerializedName("id_paciente")
    val idPaciente: Int,

    @SerializedName("nro_documento")
    val nroDocumento: String,

    val nombres: String,

    @SerializedName("apellido_paterno")
    val apellidoPaterno: String,

    @SerializedName("apellido_materno")
    val apellidoMaterno: String,

    @SerializedName("fecha_nacimiento")
    val fechaNacimiento: String,

    val sexo: Boolean,

    val estado: Boolean,

    @SerializedName("foto_perfil_url")
    val fotoPerfilUrl: String,

    @SerializedName("contacto_emergencia_nombre")
    val contactoEmergenciaNombre: String,

    @SerializedName("contacto_emergencia_telefono")
    val contactoEmergenciaTelefono: String,

    @SerializedName("id_tipo_documento")
    val idTipoDocumento: Int,

    @SerializedName("fecha_creacion")
    val fechaCreacion: String,

    @SerializedName("fecha_modificacion")
    val fechaModificacion: String
)
