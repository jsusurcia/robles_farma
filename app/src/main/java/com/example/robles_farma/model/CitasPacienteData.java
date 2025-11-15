package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class CitasPacienteData {
    @SerializedName("id_cita")
    private int idCita;
    @SerializedName("id_personal_especialidad")
    private int idPersonal;
    @SerializedName("nombre_personal")
    private String nombrePersonal;
    @SerializedName("nombre_especialidad")
    private String especialidad;
    @SerializedName("fecha")
    private String fecha;
    @SerializedName("hora_inicio")
    private String hora;
    @SerializedName("direccion_domicilio")
    private String ubicacion;
    @SerializedName("estado_cita")
    private String estado;
    @SerializedName("id_paciente")
    private int idPaciente;
    @SerializedName("nombre_paciente")
    private String nombrePaciente;
    @SerializedName("apellido_paciente")
    private String apellidoPaciente;

    public CitasPacienteData(int idCita, int idPersonal, String nombrePersonal, String especialidad, String fecha, String hora, String ubicacion, String estado, int idPaciente, String nombrePaciente, String apellidoPaciente) {
        this.idCita = idCita;
        this.idPersonal = idPersonal;
        this.nombrePersonal = nombrePersonal;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.idPaciente = idPaciente;
        this.nombrePaciente = nombrePaciente;
        this.apellidoPaciente = apellidoPaciente;
    }

    public int getIdCita() {
        return idCita;
    }

    public int getIdPersonal() {
        return idPersonal;
    }

    public String getNombrePersonal() {
        return nombrePersonal;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public String getApellidoPaciente() {
        return apellidoPaciente;
    }
}
