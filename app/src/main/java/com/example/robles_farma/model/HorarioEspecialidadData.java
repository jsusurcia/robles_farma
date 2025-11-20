package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class HorarioEspecialidadData {
    @SerializedName("id_personal")
    private int idPersonal;
    @SerializedName("nombre_completo")
    private String nombreCompleto;
    private BloqueHorarioDisponibleData[] horarios;

    public HorarioEspecialidadData(int idPersonal, String nombreCompleto, BloqueHorarioDisponibleData[] horarios) {
        this.idPersonal = idPersonal;
        this.nombreCompleto = nombreCompleto;
        this.horarios = horarios;
    }

    public int getIdPersonal() {
        return idPersonal;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public BloqueHorarioDisponibleData[] getHorarios() {
        return horarios;
    }
}
