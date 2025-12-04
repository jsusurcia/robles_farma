package com.example.robles_farma.model;

import com.google.gson.annotations.SerializedName;

public class HorarioItem {
    private int id_horario;
    private String hora_inicio;
    private String hora_fin;
    private String nombre_centro_medico;
    private String direccion_centro_medico;

    private String piso;
    private String sala;

    @SerializedName("telefono_centro_medico")
    private String telefonoCentroMedico;



    // --- GETTERS ---
    public int getId_horario() { return id_horario; }
    public String getHora_inicio() { return hora_inicio; }
    public String getHora_fin() { return hora_fin; }
    public String getNombre_centro_medico() { return nombre_centro_medico; }
    public String getDireccion_centro_medico() { return direccion_centro_medico; }

    public String getPiso() { return piso; }
    public String getSala() { return sala; }
    public String getTelefonoCentroMedico() { return telefonoCentroMedico; }
}