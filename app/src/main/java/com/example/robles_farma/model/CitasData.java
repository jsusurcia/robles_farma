package com.example.robles_farma.model;

public class CitasData {
    private String nombrePersonal;
    private String especialidad;
    private String fecha;
    private String hora;
    private String ubicacion;
    private String estado;

    private String chatId;


    public CitasData(String nombrePersonal, String especialidad, String fecha, String hora, String ubicacion, String estado) {
        this.nombrePersonal = nombrePersonal;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
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

    public String getHora() {
        return hora;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
