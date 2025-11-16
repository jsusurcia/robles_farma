package com.example.robles_farma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robles_farma.R;
import com.example.robles_farma.model.BloqueHorarioDisponibleData;
import com.google.android.material.chip.Chip;

import java.util.List;

public class BloqueHorarioDisponibleRecyclerViewAdapter extends RecyclerView.Adapter<BloqueHorarioDisponibleRecyclerViewAdapter.ViewHolder> {
    private final List<BloqueHorarioDisponibleData> listaHorarios;
    private final Context context;

    public BloqueHorarioDisponibleRecyclerViewAdapter(List<BloqueHorarioDisponibleData> listaHorarios, Context context) {
        this.listaHorarios = listaHorarios;
        this.context = context;
    }

    @NonNull
    @Override
    public BloqueHorarioDisponibleRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bloque_horario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BloqueHorarioDisponibleRecyclerViewAdapter.ViewHolder holder, int position) {
        BloqueHorarioDisponibleData horario = listaHorarios.get(position);

        holder.chipHorario.setText(horario.getHoraInicio() + " - " + horario.getHoraFin());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Chip chipHorario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chipHorario = itemView.findViewById(R.id.chipHorario);
        }
    }
}
