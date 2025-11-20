package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class BloqueHorarioDisponibleData {
    @SerializedName("id_horario")
    private int idHorario;
    @SerializedName("hora_inicio")
    private String horaInicio;
    @SerializedName("hora_fin")
    private String horaFin;
    @SerializedName("nombre_centro_medico")
    private String nombreCentroMedico;
    @SerializedName("direccion_centro_medico")
    private String direccionCentroMedico;

    public BloqueHorarioDisponibleData(int idHorario, String horaInicio, String horaFin, String nombreCentroMedico, String direccionCentroMedico) {
        this.idHorario = idHorario;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nombreCentroMedico = nombreCentroMedico;
        this.direccionCentroMedico = direccionCentroMedico;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getNombreCentroMedico() {
        return nombreCentroMedico;
    }

    public String getDireccionCentroMedico() {
        return direccionCentroMedico;
    }
}
