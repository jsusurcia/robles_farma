package com.example.robles_farma.adapter;

import android.content.Context;
import android.util.Log;
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
    private int seleccionado = RecyclerView.NO_POSITION;
    private int seleccionadoPosicion = -1;
    private OnHorarioClickListener listener;

    public interface OnHorarioClickListener {
        void onHorarioClick(int idHorario);
    }

    public BloqueHorarioDisponibleRecyclerViewAdapter(List<BloqueHorarioDisponibleData> listaHorarios, Context context, OnHorarioClickListener listener) {
        this.listaHorarios = listaHorarios;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BloqueHorarioDisponibleRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chip_horario_reprogramacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BloqueHorarioDisponibleRecyclerViewAdapter.ViewHolder holder, int position) {
        BloqueHorarioDisponibleData horario = listaHorarios.get(position);
        holder.chipHorario.setText(horario.getHoraInicio() + " - " + horario.getHoraFin());

        if (position == seleccionado) {
            holder.chipHorario.setChipBackgroundColorResource(R.color.confirmada_color);
            holder.chipHorario.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.chipHorario.setChipBackgroundColorResource(R.color.white);
            holder.chipHorario.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.chipHorario.setOnClickListener(v -> {
            if (seleccionado != RecyclerView.NO_POSITION) {
                notifyItemChanged(seleccionado);
            }

            listener.onHorarioClick(horario.getIdHorario());
            seleccionado = holder.getAdapterPosition();
            Log.d("API_SUCCESS", "Horario seleccionado: " + horario.getHoraInicio() + " en posicion: " + seleccionado);
            notifyItemChanged(seleccionado);
        });
    }

    @Override
    public int getItemCount() {
        return listaHorarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Chip chipHorario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chipHorario = itemView.findViewById(R.id.chipHorario);
        }
    }

    public BloqueHorarioDisponibleData getHorarioSeleccionado() {
        if (seleccionado != RecyclerView.NO_POSITION) {
            return listaHorarios.get(seleccionado);
        }
        return null;
    }
}