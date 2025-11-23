package com.example.robles_farma.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Importar Glide
import com.example.robles_farma.R;
import com.example.robles_farma.model.CitasPacienteData;
import com.example.robles_farma.response.FotoUploadResponse;
import com.example.robles_farma.retrofit.ApiService; //
import com.example.robles_farma.retrofit.ChatService;
import com.example.robles_farma.retrofit.RetrofitClient; //
import com.google.android.material.chip.Chip;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitasRecyclerViewAdapter extends RecyclerView.Adapter<CitasRecyclerViewAdapter.ViewHolder> {
    private final List<CitasPacienteData> listaCitas;
    private final Context context;
    private final boolean isPastCitas;

    public CitasRecyclerViewAdapter(List<CitasPacienteData> listaCitas, Context context) {
        this(listaCitas, context, false);
    }

    public CitasRecyclerViewAdapter(List<CitasPacienteData> listaCitas, Context context, boolean isPastCitas) {
        this.listaCitas = listaCitas;
        this.context = context;
        this.isPastCitas = isPastCitas;
    }

    @NonNull
    @Override
    public CitasRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_citas, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasRecyclerViewAdapter.ViewHolder holder, int position) {
        CitasPacienteData cita = listaCitas.get(position);

        // 1. Cargar textos
        holder.textDoctorName.setText(cita.getNombrePersonal());
        holder.textSpecialty.setText(cita.getEspecialidad());
        holder.textDate.setText(cita.getFecha());
        holder.textHour.setText(cita.getHora());
        holder.textLocation.setText(cita.getUbicacion() != null ? cita.getUbicacion() : "Centro Médico");
        holder.chipStatus.setText(cita.getEstado());

        // 2. --- NUEVO: Cargar Foto del Doctor ---
        // Reseteamos la imagen por defecto para evitar que se mezclen al hacer scroll
        holder.imageDoctor.setImageResource(R.drawable.default_doctor_image);

        int doctorId = cita.getIdPersonal();
        if (doctorId > 0) {
            // Llamamos al servicio. Asumimos que usas 'getFotoMedico' que recibe el ID del doctor.
            // Si tu backend usa el ID de "PersonalEspecialidad" para citas, cambia esto por 'getFotoPersonal'
            ApiService apiService = RetrofitClient.createService();

            Log.e("PERSONALINI", "ID: " + doctorId);

            apiService.getFotoPersonal(doctorId).enqueue(new Callback<FotoUploadResponse>() {
                @Override
                public void onResponse(@NonNull Call<FotoUploadResponse> call, @NonNull Response<FotoUploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String url = response.body().getUrl();

                        // Validar que la URL y el contexto existan
                        if (url != null && !url.isEmpty() && context != null) {
                            Glide.with(context)
                                    .load(url)
                                    .placeholder(R.drawable.default_doctor_image) // Mientras carga
                                    .error(R.drawable.default_doctor_image)       // Si falla
                                    .into(holder.imageDoctor);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<FotoUploadResponse> call, @NonNull Throwable t) {
                    // Si falla, se queda la imagen por defecto
                    Log.e("CitasAdapter", "Error cargando foto doctor: " + t.getMessage());
                }
            });
        }
        // ---------------------------------------

        if (isPastCitas) {
            holder.iconInfo.setVisibility(View.GONE);
        } else {
            holder.iconInfo.setVisibility(View.VISIBLE);
        }

        // Colores según estado
        int colorResId;
        String estado = cita.getEstado() != null ? cita.getEstado().toLowerCase() : "";
        switch (estado) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView imageDoctor;
        TextView textDoctorName, textSpecialty, textDate, textHour, textLocation;
        Chip chipStatus;
        ImageView iconInfo, iconMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageDoctor = itemView.findViewById(R.id.imageDoctor);
            textDoctorName = itemView.findViewById(R.id.textDoctorName);
            textSpecialty = itemView.findViewById(R.id.textSpecialty);
            textDate = itemView.findViewById(R.id.textDate);
            textHour = itemView.findViewById(R.id.textHour);
            textLocation = itemView.findViewById(R.id.textLocation);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            iconInfo = itemView.findViewById(R.id.iconInfo);
            iconMessage = itemView.findViewById(R.id.iconMessage);

            iconInfo.setOnClickListener(this);
            iconMessage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            CitasPacienteData cita = listaCitas.get(position);
            int idCita = cita.getIdCita();
            int idPersonal = cita.getIdPersonal();
            int idEspecialidad = cita.getIdEspecialidad();
            String doctorName = cita.getNombrePersonal();
            String specialty = cita.getEspecialidad();
            String date = cita.getFecha();
            String hour = cita.getHora();
            String location = cita.getUbicacion();
            boolean enCentroMedico = cita.isEnCentroMedico();

            if (v.getId() == R.id.iconInfo) {
                Bundle args = new Bundle();
                args.putInt("idCita", idCita);
                args.putInt("idPersonal", idPersonal);
                args.putInt("idEspecialidad", idEspecialidad);
                args.putString("doctorName", doctorName);
                args.putString("specialty", specialty);
                args.putString("date", date);
                args.putString("hour", hour);
                args.putString("location", location);
                args.putBoolean("enCentroMedico", enCentroMedico);

                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_citas_to_navigation_detalle_cita, args);

            } else if (v.getId() == R.id.iconMessage) {
                int doctorId = cita.getIdPersonal();

                if (doctorId <= 0) {
                    Toast.makeText(v.getContext(), "No se encontró el ID del doctor", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatService chatService = com.example.robles_farma.retrofit.RetrofitClient
                        .createService(v.getContext(), ChatService.class);

                // Crear request para el backend
                com.example.robles_farma.request.ChatCreateRequest request =
                        new com.example.robles_farma.request.ChatCreateRequest(String.valueOf(doctorId));

                chatService.createOrGetChat(request).enqueue(new retrofit2.Callback<com.example.robles_farma.response.ChatResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<com.example.robles_farma.response.ChatResponse> call,
                            retrofit2.Response<com.example.robles_farma.response.ChatResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            String chatId = response.body().getChatId();

                            Bundle args = new Bundle();
                            args.putString("chatId", chatId);
                            args.putString("doctorId", String.valueOf(doctorId));
                            args.putString("doctorName", doctorName);

                            try {
                                Navigation.findNavController(v)
                                        .navigate(R.id.action_navigation_citas_to_navigation_chat_detail, args);

                                Log.i("CitasAdapter", "Chat abierto con el Dr. " + doctorName + " (ID: " + doctorId + ")");
                            } catch (Exception e) {
                                Log.e("CitasAdapter", "Error al navegar al chat: " + e.getMessage());
                            }
                        } else {
                            Toast.makeText(v.getContext(), "⚠No se pudo crear o abrir el chat", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.robles_farma.response.ChatResponse> call, Throwable t) {
                        Log.e("CitasAdapter", "Error al abrir chat: " + t.getMessage());
                        Toast.makeText(v.getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}