package com.example.robles_farma.model;

public class CitasData {
    private String nombrePersonal;
    private String especialidad;
    private String fecha;
    private String ubicacion;
    private String estado;

    public CitasData(String nombrePersonal, String especialidad, String fecha, String ubicacion, String estado) {
        this.nombrePersonal = nombrePersonal;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.estado = estado;
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

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }
}
