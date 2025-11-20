package com.example.robles_farma.model;

public class HorarioItem {
    private int id_horario;
    private String hora_inicio; // El backend lo manda como String en el schema HorarioItem
    private String hora_fin;
    private String nombre_centro_medico;
    private String direccion_centro_medico;

    // Getters
    public int getId_horario() { return id_horario; }
    public String getHora_inicio() { return hora_inicio; }
    public String getHora_fin() { return hora_fin; }
    public String getNombre_centro_medico() { return nombre_centro_medico; }
    public String getDireccion_centro_medico() { return direccion_centro_medico; }
}
