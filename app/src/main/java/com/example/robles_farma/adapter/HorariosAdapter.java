package com.example.robles_farma.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.robles_farma.R;
import com.example.robles_farma.model.HorarioItem;

import java.util.ArrayList;
import java.util.List;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder> {

    // Clase auxiliar para tener el horario + nombre del doctor en un solo objeto visual
    public static class HorarioDisplay {
        public HorarioItem horario;
        public String nombreDoctor;

        public HorarioDisplay(HorarioItem horario, String nombreDoctor) {
            this.horario = horario;
            this.nombreDoctor = nombreDoctor;
        }
    }

    private List<HorarioDisplay> listaHorarios = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HorarioDisplay item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDatos(List<HorarioDisplay> nuevosDatos) {
        this.listaHorarios = nuevosDatos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_bloque_horario, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        HorarioDisplay item = listaHorarios.get(position);

        // Formatear horas (quitar segundos)
        String inicio = item.horario.getHora_inicio();
        if(inicio.length() > 5) inicio = inicio.substring(0, 5);

        String fin = item.horario.getHora_fin();
        if(fin.length() > 5) fin = fin.substring(0, 5);

        holder.tvRangoHora.setText(inicio + " - " + fin);
        holder.tvNombreDoctor.setText(item.nombreDoctor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return listaHorarios.size();
    }

    static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvRangoHora, tvNombreDoctor;

        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate que estos IDs coincidan con tu XML card_bloque_horario
            tvRangoHora = itemView.findViewById(R.id.tv_hora_rango);
            tvNombreDoctor = itemView.findViewById(R.id.tv_nombre_doctor);
        }
    }
}