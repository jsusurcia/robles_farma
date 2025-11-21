package com.example.robles_farma.ui.citas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentResumenCitaBinding;
import com.example.robles_farma.request.CitaCreateRequest;
import com.example.robles_farma.response.CitaResponse;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumenCitaFragment extends Fragment {

    private FragmentResumenCitaBinding binding;
    private ApiService apiService; // 1. Variable para la API

    // Variables para recibir datos
    private int idHorario;
    private String nombreDoctor;
    private String especialidad;
    private String fecha;
    private String hora;
    private boolean esDomicilio; // true = domicilio, false = centro medico

    // Datos opcionales (Centro Medico)
    private String nombreCentro;
    private String direccionCentro;
    private double precioReal; // Variable global
    private String piso;
    private String sala;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResumenCitaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. Inicializamos la API
        apiService = RetrofitClient.createService();

        recibirDatos();
        configurarUI();

        // 3. Configuramos el botón para llamar al método de reserva
        binding.btnConfirmarFinal.setOnClickListener(v -> {
            realizarReserva();
        });
    }

    private void recibirDatos() {
        if (getArguments() != null) {
            idHorario = getArguments().getInt("id_horario");
            nombreDoctor = getArguments().getString("nombre_doctor");
            especialidad = getArguments().getString("especialidad");
            fecha = getArguments().getString("fecha");
            hora = getArguments().getString("hora");
            precioReal = getArguments().getDouble("precio_consulta", 0.0);
            // OJO: Aquí invertimos la lógica del boolean que viene del fragment anterior
            // Si en_centro_medico es FALSE, entonces esDomicilio es TRUE
            boolean enCentro = getArguments().getBoolean("en_centro_medico");
            esDomicilio = !enCentro;

            nombreCentro = getArguments().getString("nombre_centro");
            direccionCentro = getArguments().getString("direccion_centro");
            piso = getArguments().getString("piso");
            sala = getArguments().getString("sala");
        }
    }

    private void configurarUI() {
        binding.tvResumenDoctor.setText(nombreDoctor);
        binding.tvResumenEspecialidad.setText(especialidad);
        binding.tvResumenFecha.setText(fecha);
        binding.tvResumenHora.setText(hora);
        binding.tvCosteTotal.setText("S/ " + String.format("%.2f", precioReal));
        if (esDomicilio) {
            // MODO DOMICILIO
            binding.tvNombreCentroMedico.setVisibility(View.GONE);
            binding.tvLabelDomicilio.setVisibility(View.VISIBLE);

            binding.layoutDetallesCentro.setVisibility(View.GONE);
            binding.layoutDetallesDomicilio.setVisibility(View.VISIBLE);
        } else {
            // MODO CENTRO MEDICO
            binding.tvNombreCentroMedico.setVisibility(View.VISIBLE);
            binding.tvLabelDomicilio.setVisibility(View.GONE);

            binding.layoutDetallesCentro.setVisibility(View.VISIBLE);
            binding.layoutDetallesDomicilio.setVisibility(View.GONE);

            binding.tvNombreCentroMedico.setText(nombreCentro != null ? nombreCentro : "Centro Médico");
            binding.tvDireccionCentro.setText(direccionCentro != null ? direccionCentro : "-");
            String textoUbicacion = (piso != null ? piso : "-") + " / " + (sala != null ? sala : "-");
            binding.tvPisoSala.setText(textoUbicacion);

        }
    }

    // 4. El método para guardar en la Base de Datos
    private void realizarReserva() {
        String direccionEnvio = null;

        // Validación: Si es domicilio, la dirección es obligatoria
        if (esDomicilio) {
            direccionEnvio = binding.etDireccionDomicilio.getText().toString().trim();
            if (direccionEnvio.isEmpty()) {
                binding.etDireccionDomicilio.setError("Ingrese su dirección exacta");
                return;
            }
        }

        // Crear el objeto Request (Usa las clases con @SerializedName que creamos)
        CitaCreateRequest request = new CitaCreateRequest(idHorario, direccionEnvio);

        // Bloquear botón para evitar doble clic
        binding.btnConfirmarFinal.setEnabled(false);
        binding.btnConfirmarFinal.setText("Procesando...");

        // Llamada a la API
        Call<ItemResponse<CitaResponse>> call = apiService.createCita(request);

        call.enqueue(new Callback<ItemResponse<CitaResponse>>() {
            @Override
            public void onResponse(Call<ItemResponse<CitaResponse>> call, Response<ItemResponse<CitaResponse>> response) {
                binding.btnConfirmarFinal.setEnabled(true);
                binding.btnConfirmarFinal.setText("Confirmar cita");

                if (response.isSuccessful() && response.body() != null) {
                    // ÉXITO
                    Toast.makeText(getContext(), "¡Cita reservada con éxito!", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("nombre_doctor", nombreDoctor);
                    bundle.putString("fecha", fecha);
                    bundle.putString("hora", hora);

                    // Ubicación depende de la lógica
                    String ubi = esDomicilio ? binding.etDireccionDomicilio.getText().toString() : direccionCentro;                    bundle.putString("ubicacion", ubi);

                    // Precio y el QR que viene de la respuesta de la API
                    bundle.putDouble("precio", precioReal); // O el precio que mostraste
                    bundle.putString("codigo_qr_data", response.body().getData().getCodigoQr()); // <-- QR de la API
                    try {
                        Navigation.findNavController(requireView()).navigate(R.id.action_resumen_to_confirmada, bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // ERROR DEL SERVIDOR (Ej. 409 Horario Ocupado)
                    String errorMsg = "Error al reservar";
                    if (response.code() == 409) errorMsg = "El horario ya fue ocupado por otra persona.";
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ItemResponse<CitaResponse>> call, Throwable t) {
                binding.btnConfirmarFinal.setEnabled(true);
                binding.btnConfirmarFinal.setText("Confirmar cita");
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}