package com.example.robles_farma.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.robles_farma.R;
import com.example.robles_farma.model.HorarioItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder> {

    // Clase auxiliar para tener el horario + nombre del doctor en un solo objeto visual
    public static class HorarioDisplay {
        public HorarioItem horario;
        public String nombreDoctor;
        public double precio;
        public String telefono;

        public HorarioDisplay(HorarioItem horario, String nombreDoctor, double precio, String telefono) {
            this.horario = horario;
            this.nombreDoctor = nombreDoctor;
            this.precio = precio;
            this.telefono= telefono;
        }
    }

    private List<HorarioDisplay> listaHorarios = new ArrayList<>();
    private OnItemClickListener listener;
    private int selectedPosition=-1;

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
    public void onBindViewHolder(@NonNull HorarioViewHolder holder,@SuppressLint("RecyclerView") int position) {
        HorarioDisplay item = listaHorarios.get(position);
        Context context = holder.itemView.getContext();


        // Formatear horas (quitar segundos)
        String inicio = item.horario.getHora_inicio();
        if(inicio.length() > 5) inicio = inicio.substring(0, 5);

        String fin = item.horario.getHora_fin();
        if(fin.length() > 5) fin = fin.substring(0, 5);

        holder.tvRangoHora.setText(inicio + " - " + fin);
        holder.tvNombreDoctor.setText(item.nombreDoctor);

        if (selectedPosition == position) {
            // ESTILO SELECCIONADO (Fondo Verde, Texto Blanco)
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_primary));
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.color_primary)); // Borde del mismo color

            holder.tvRangoHora.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tvNombreDoctor.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            // ESTILO NORMAL (Fondo Blanco, Texto Negro/Gris)
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, android.R.color.darker_gray)); // Borde gris

            holder.tvRangoHora.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.tvNombreDoctor.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
        }
        // 3. CLICK LISTENER
        holder.itemView.setOnClickListener(v -> {
            // Guardamos la posición anterior para actualizar solo esa fila
            int previousItem = selectedPosition;
            selectedPosition = position;

            // Notificamos cambios:
            // 1. Actualizar el ítem que antes estaba seleccionado (para que se vuelva blanco)
            notifyItemChanged(previousItem);
            // 2. Actualizar el nuevo ítem seleccionado (para que se vuelva verde)
            notifyItemChanged(selectedPosition);

            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return listaHorarios.size();
    }

    static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvRangoHora, tvNombreDoctor;
        MaterialCardView cardView;

        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate que estos IDs coincidan con tu XML card_bloque_horario
            tvRangoHora = itemView.findViewById(R.id.tv_hora_rango);
            tvNombreDoctor = itemView.findViewById(R.id.tv_nombre_doctor);
            cardView = itemView.findViewById(R.id.card_container);
        }
    }
}