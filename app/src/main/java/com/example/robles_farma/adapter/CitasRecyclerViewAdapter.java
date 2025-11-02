package com.example.robles_farma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robles_farma.R;
import com.example.robles_farma.model.CitasData;
import com.google.android.material.chip.Chip;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CitasRecyclerViewAdapter extends RecyclerView.Adapter<CitasRecyclerViewAdapter.ViewHolder> {
    private List<CitasData> listaCitas;
    private Context context;

    public CitasRecyclerViewAdapter(List<CitasData> listaCitas, Context context) {
        this.listaCitas = listaCitas;
        this.context = context;
    }

    @NonNull
    @Override
    public CitasRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_citas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasRecyclerViewAdapter.ViewHolder holder, int position) {
        CitasData cita = listaCitas.get(position);

        // Aquí puedes establecer la imagen del doctor si la tienes
        holder.imageDoctor.setImageResource(R.drawable.default_doctor_image);
        holder.textDoctorName.setText(cita.getNombrePersonal());
        holder.textSpecialty.setText(cita.getEspecialidad());
        holder.textDate.setText(cita.getFecha());
        holder.textLocation.setText(cita.getUbicacion());
        holder.chipStatus.setText(cita.getEstado());

        // Lógica para cambiar el color del Chip según el estado
        int colorResId;
        switch (cita.getEstado().toLowerCase()) {
            case "confirmada":
                colorResId = R.color.confirmada_color;
                break;
            case "pendiente":
                colorResId = R.color.pendiente_color;
                break;
            case "cancelada":
                colorResId = R.color.cancelada_color;
                break;
            default:
                colorResId = R.color.gray;
                break;
        }
        holder.chipStatus.setChipBackgroundColorResource(colorResId);
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView imageDoctor;
        TextView textDoctorName, textSpecialty, textDate, textLocation;
        Chip chipStatus;
        ImageView iconInfo, iconMessage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageDoctor = itemView.findViewById(R.id.imageDoctor);
            textDoctorName = itemView.findViewById(R.id.textDoctorName);
            textSpecialty = itemView.findViewById(R.id.textSpecialty);
            textDate = itemView.findViewById(R.id.textDate);
            textLocation = itemView.findViewById(R.id.textLocation);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            iconInfo = itemView.findViewById(R.id.iconInfo);
            iconMessage = itemView.findViewById(R.id.iconMessage);

            itemView.setOnClickListener(this);
            iconInfo.setOnClickListener(this);
            iconMessage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.iconInfo) {
                Toast.makeText(v.getContext(), "Ver detalle de la cita", Toast.LENGTH_SHORT).show();
            } else if (v.getId() == R.id.iconMessage) {
                Toast.makeText(v.getContext(), "Redirigiendo al chat del doctor", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
