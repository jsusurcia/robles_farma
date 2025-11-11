package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class CitasData {
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


    public CitasData(String nombrePersonal, String especialidad, String fecha, String hora, String ubicacion, String estado) {
        this.nombrePersonal = nombrePersonal;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdPersonal() {
        return idPersonal;
    }

    public void setIdPersonal(int idPersonal) {
        this.idPersonal = idPersonal;
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
}
