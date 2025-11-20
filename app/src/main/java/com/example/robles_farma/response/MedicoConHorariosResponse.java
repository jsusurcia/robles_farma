package com.example.robles_farma.response;

import com.example.robles_farma.model.HorarioItem; // Asegúrate de tener HorarioItem en 'model'
import java.util.List;

public class MedicoConHorariosResponse {
    private int id_personal;
    private String nombre_completo;
    private List<HorarioItem> horarios;

    public int getId_personal() { return id_personal; }
    public String getNombre_completo() { return nombre_completo; }
    public List<HorarioItem> getHorarios() { return horarios; }
}