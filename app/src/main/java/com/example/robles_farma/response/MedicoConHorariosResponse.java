package com.example.robles_farma.response;

import com.example.robles_farma.model.HorarioItem;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MedicoConHorariosResponse {
    @SerializedName("id_personal")
    private int idPersonal;

    @SerializedName("nombre_completo")
    private String nombreCompleto;

    private double precio;

    @SerializedName("horarios")
    private List<HorarioItem> horarios;

    // Getters
    public int getIdPersonal() { return idPersonal; }
    public String getNombreCompleto() { return nombreCompleto; }
    public double getPrecio() { return precio; }
    public List<HorarioItem> getHorarios() { return horarios; }
}