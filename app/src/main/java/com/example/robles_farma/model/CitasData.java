package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class CitasData {
    @SerializedName("id_cita")
    private int idCita;
    @SerializedName("id_paciente")
    private int idPaciente;
    @SerializedName("id_personal_especialidad")
    private int idPersonalEspecialidad;
    @SerializedName("id_horario")
    private int idHorario;
    @SerializedName("direccion_domicilio")
    private String direccionDomicilio;
    @SerializedName("estado_cita")
    private String estadoCita;
    @SerializedName("estado_viaje")
    private String estadoViaje;
    @SerializedName("fecha_creacion")
    private String fechaCreacion;
    @SerializedName("fecha_modificacion")
    private String fechaModificacion;
    @SerializedName("codigo_qr")
    private String codigoQr;
    @SerializedName("duracion_minutos")
    private int duracionMinutos;

    public int getIdCita() {
        return idCita;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public int getIdPersonalEspecialidad() {
        return idPersonalEspecialidad;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public String getDireccionDomicilio() {
        return direccionDomicilio;
    }

    public String getEstadoCita() {
        return estadoCita;
    }

    public String getEstadoViaje() {
        return estadoViaje;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }
}
