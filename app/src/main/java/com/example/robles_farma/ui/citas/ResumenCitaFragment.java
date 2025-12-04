package com.example.robles_farma.ui.citas;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentResumenCitaBinding;
import com.example.robles_farma.model.CitaResumenData;
import com.example.robles_farma.request.CitaCreateRequest;
import com.example.robles_farma.request.ReservaRequest;
import com.example.robles_farma.response.CitaResponse;
import com.example.robles_farma.response.FotoUploadResponse;
import com.example.robles_farma.response.ItemListResponse;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.PacienteAseguradoResponse;
import com.example.robles_farma.response.ReservaResponse;
import com.example.robles_farma.response.TipoPagoResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;
import com.google.gson.internal.NonNullElementWrapperList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumenCitaFragment extends Fragment {

    // --- 1. VARIABLES GLOBALES ---
    private FragmentResumenCitaBinding binding;
    private ApiService apiService;
    private LoginStorage loginStorage;

    // Datos de la Cita (Recibidos del bundle)
    private int idHorario;
    private String idPaciente;
    private String nombreDoctor;
    private String especialidad;
    private String fecha;
    private String hora;
    private boolean esDomicilio;
    private double precioReal;

    // Datos del Centro Médico (Opcionales)
    private String nombreCentro;
    private String direccionCentro;
    private String piso;
    private String sala;

    private String telefono;

    // Datos de Pago
    private int idMetodoPagoSeleccionado = -1; // -1 indica que no se ha seleccionado nada
    private List<TipoPagoResponse> tiposPago;

    // --- 2. CICLO DE VIDA DEL FRAGMENT ---

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentResumenCitaBinding.inflate(inflater, container, false);
        loginStorage = new LoginStorage(getContext());
        //paciente = loginStorage.getPaciente();
        idPaciente = loginStorage.getUserId(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar API
        apiService = RetrofitClient.createService();

        // 1. Obtener datos y configurar la vista inicial
        recibirDatos();
        configurarUI();
        cargarFotoDoctor();

        // 2. Cargar lógica de pagos
        cargarTiposPagos(); // (Opcional si usas IDs fijos)
        configurarListenersPago();

        // 3. Configurar Botón de Confirmación
        binding.btnConfirmarFinal.setOnClickListener(v -> {
            // AQUÍ CAMBIAS: Usar el método nuevo o el antiguo
            realizarReservaCita();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // --- 3. CONFIGURACIÓN Y DATOS (SETUP) ---

    private void recibirDatos() {
        if (getArguments() != null) {
            idHorario = getArguments().getInt("id_horario");
            nombreDoctor = getArguments().getString("nombre_doctor");
            especialidad = getArguments().getString("especialidad");
            fecha = getArguments().getString("fecha");
            hora = getArguments().getString("hora");
            precioReal = getArguments().getDouble("precio_consulta", 0.0);
            telefono = getArguments().getString("telefono_centro");

            // Lógica inversa: si no es en centro médico, es domicilio
            boolean enCentro = getArguments().getBoolean("en_centro_medico");
            esDomicilio = !enCentro;

            nombreCentro = getArguments().getString("nombre_centro");
            direccionCentro = getArguments().getString("direccion_centro");
            piso = getArguments().getString("piso");
            sala = getArguments().getString("sala");
        }
    }

    private void calcularDescuento() {
        Call<ItemResponse<PacienteAseguradoResponse>> call = apiService.esAsegurado(Integer.parseInt(idPaciente));
        call.enqueue(new Callback<ItemResponse<PacienteAseguradoResponse>>() {
            @Override
            public void onResponse(Call<ItemResponse<PacienteAseguradoResponse>> call, Response<ItemResponse<PacienteAseguradoResponse>> response) {
                if (response.isSuccessful()) {
                    Log.d("TEST_RESERVA", "PACIENTE ASEGURADO?: " + response.body().getData().isEsAsegurado());
                    boolean esAsegurado = response.body().getData().isEsAsegurado();
                    if (esAsegurado) {
                        //Guardar el precio original
                        double precioOriginal = precioReal;
                        //Calcular el precio con descuento
                        precioReal = precioReal - (precioReal * 0.20);
                        //Configuraciones para el UI
                        binding.tvPrecioOriginal.setText("S/ " + String.format("%.2f", precioOriginal));
                        binding.tvPrecioOriginal.setPaintFlags(binding.tvPrecioOriginal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        binding.tvPrecioOriginal.setVisibility(View.VISIBLE);
                        binding.tvCosteTotal.setText("S/ " + String.format("%.2f", precioReal));
                    }
                } else {
                    String errorMsg = "Error al obtener el estado del seguro del paciente";
                    Log.e("TEST_RESERVA", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ItemResponse<PacienteAseguradoResponse>> call, Throwable t) {
                Log.e("TEST_RESERVA", "Error de conexión: " + t.getMessage());
                binding.tvPrecioOriginal.setVisibility(View.GONE);
                binding.tvCosteTotal.setText("S/ " + String.format("%.2f", precioReal));
            }
        });
    }

    private void configurarUI() {
        binding.tvResumenDoctor.setText(nombreDoctor);
        binding.tvResumenEspecialidad.setText(especialidad);
        binding.tvResumenFecha.setText(fecha);
        binding.tvResumenHora.setText(hora);
        binding.tvCosteTotal.setText("S/ " + String.format("%.2f", precioReal));
        calcularDescuento();

        if (esDomicilio) {
            // MODO DOMICILIO
            binding.tvNombreCentroMedico.setVisibility(View.GONE);
            binding.tvLabelDomicilio.setVisibility(View.VISIBLE);
            binding.layoutDetallesCentro.setVisibility(View.GONE);
            binding.layoutDetallesDomicilio.setVisibility(View.VISIBLE);
            binding.btnLlamarCentro.setVisibility(View.GONE);
        } else {
            // MODO CENTRO MÉDICO
            binding.tvNombreCentroMedico.setVisibility(View.VISIBLE);
            binding.tvLabelDomicilio.setVisibility(View.GONE);
            binding.layoutDetallesCentro.setVisibility(View.VISIBLE);
            binding.layoutDetallesDomicilio.setVisibility(View.GONE);

            binding.tvNombreCentroMedico.setText(nombreCentro != null ? nombreCentro : "Centro Médico");
            binding.tvDireccionCentro.setText(direccionCentro != null ? direccionCentro : "-");
            String textoUbicacion = (piso != null ? piso : "-") + " / " + (sala != null ? sala : "-");
            binding.tvPisoSala.setText(textoUbicacion);
            if (telefono != null && !telefono.isEmpty()){
                binding.btnLlamarCentro.setVisibility(View.VISIBLE);
                binding.btnLlamarCentro.setOnClickListener(v ->{
                    try {
                        Uri number = Uri.parse("tel:" + telefono);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL,number);
                        startActivity(callIntent);
                    }catch(Exception e){
                        Toast.makeText(getContext(), "No se puede realizar una llamada al centro médico", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                binding.btnLlamarCentro.setVisibility(View.GONE);
            }
        }
    }

    private void cargarFotoDoctor() {
        if (idHorario > 0) {
            apiService.getFotoPersonalPorHorario(idHorario).enqueue(new Callback<FotoUploadResponse>() {
                @Override
                public void onResponse(@NonNull Call<FotoUploadResponse> call, @NonNull Response<FotoUploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null && getContext() != null) {
                        String url = response.body().getUrl();
                        if (url != null && !url.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(url)
                                    .placeholder(R.drawable.default_doctor_image)
                                    .error(R.drawable.default_doctor_image)
                                    .circleCrop()
                                    .into(binding.imgResumenDoctor);
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<FotoUploadResponse> call, @NonNull Throwable t) {
                    Log.e("ResumenCita", "Error cargando foto: " + t.getMessage());
                }
            });
        }
    }

    // --- 4. LÓGICA DE INTERFAZ (LISTENERS) ---

    private void configurarListenersPago() {
        // A. Listener para los RadioButtons (Selección de Pago)
        binding.rgMetodoPago.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_efectivo) {
                idMetodoPagoSeleccionado = 1; // ID BD para Efectivo
                mostrarFormulario(1);
            } else if (checkedId == R.id.rb_tarjeta) {
                idMetodoPagoSeleccionado = 2; // ID BD para Tarjeta
                mostrarFormulario(2);
            } else if (checkedId == R.id.rb_yape) {
                idMetodoPagoSeleccionado = 3; // ID BD para Yape
                mostrarFormulario(3);
            }
        });

        // B. Formateo automático de Fecha de Expiración (MM/AA) para Tarjeta
        binding.etFechaExpiracion.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;
                String text = s.toString().replaceAll("[^0-9]", "");
                if (text.length() > 4) text = text.substring(0, 4);

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < text.length(); i++) {
                    if (i == 2) formatted.append("/");
                    formatted.append(text.charAt(i));
                }
                isUpdating = true;
                binding.etFechaExpiracion.setText(formatted.toString());
                binding.etFechaExpiracion.setSelection(formatted.length());
                isUpdating = false;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Metodo auxiliar para mostrar/ocultar layouts según selección
    private void mostrarFormulario(int tipo) {
        binding.layoutDetallesPago.setVisibility(View.VISIBLE);

        // 1: Efectivo, 2: Tarjeta, 3: Yape
        binding.layoutEfectivo.setVisibility(tipo == 1 ? View.VISIBLE : View.GONE);
        binding.layoutTarjeta.setVisibility(tipo == 2 ? View.VISIBLE : View.GONE);
        binding.layoutYape.setVisibility(tipo == 3 ? View.VISIBLE : View.GONE);
    }

    // --- 5. ACCIONES DE RESERVA (API) ---

    /**
     * METODO NUEVO: Validación y Log de datos para la nueva estructura transaccional (Cita + Pago)
     */
    private void realizarReservaCita() {
        String direccionEnvio = null;

        // Validar Dirección (si es domicilio)
        if (esDomicilio) {
            direccionEnvio = binding.etDireccionDomicilio.getText().toString().trim();
            if (direccionEnvio.isEmpty()) {
                binding.etDireccionDomicilio.setError("Ingrese su dirección exacta");
                binding.etDireccionDomicilio.requestFocus();
                return;
            }
        }

        // Validar Selección de Pago
        if (idMetodoPagoSeleccionado == -1) {
            Toast.makeText(getContext(), "Por favor, seleccione un método de pago", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar campos para YAPE
        if (idMetodoPagoSeleccionado == 3) { // Yape
            if (binding.etTelefonoYape.getText().toString().isEmpty() ||
                    binding.etCodigoOperacion.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Complete los datos de Yape", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (idMetodoPagoSeleccionado == 2) { // Tarjeta
            if (binding.etNumeroTarjeta.getText().toString().trim().isEmpty() ||
                    binding.etNombreTitular.getText().toString().trim().isEmpty() ||
                    binding.etFechaExpiracion.getText().toString().trim().isEmpty() ||
                    binding.etCvv.getText().toString().trim().isEmpty()) {

                Toast.makeText(getContext(), "Por favor, complete todos los datos de la tarjeta", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Log.d("TEST_RESERVA", "ID Paciente: " + idPaciente);
        Log.d("TEST_RESERVA", "ID Horario: " + idHorario);
        Log.d("TEST_RESERVA", "Es Domicilio: " + esDomicilio);
        Log.d("TEST_RESERVA", "Dirección: " + (direccionEnvio != null ? direccionEnvio : "EN CENTRO MÉDICO"));
        Log.d("TEST_RESERVA", "ID Tipo Pago: " + idMetodoPagoSeleccionado);

        ReservaRequest request = new ReservaRequest(idHorario, direccionEnvio, idMetodoPagoSeleccionado);
        Call<ReservaResponse> call = apiService.reservarCita(request);
        call.enqueue(new Callback<ReservaResponse>() {
            @Override
            public void onResponse(Call<ReservaResponse> call, Response<ReservaResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Cita reservada con éxito!", Toast.LENGTH_LONG).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("nombre_doctor", nombreDoctor);
                    bundle.putString("fecha", fecha);
                    bundle.putString("hora", hora);
                    String ubi = esDomicilio ? binding.etDireccionDomicilio.getText().toString() : direccionCentro;
                    bundle.putString("ubicacion", ubi);
                    bundle.putDouble("precio", response.body().getData().getComprobante().getMontoTotal());
                    bundle.putString("codigo_qr_data", response.body().getData().getCita().getCodigoQr());
                    bundle.putString("nro_comprobante", response.body().getData().getComprobante().getNroComprobante());
                    bundle.putString("fecha_emision", response.body().getData().getComprobante().getFechaEmision());
                    bundle.putDouble("igv", response.body().getData().getComprobante().getIgv());

                    try {
                        Navigation.findNavController(requireView()).navigate(R.id.action_resumen_to_confirmada, bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String errorMsg = "Error al reservar";
                    if (response.code() == 409) errorMsg = "El horario ya fue ocupado por otra persona.";
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReservaResponse> call, Throwable t) {
                binding.btnConfirmarFinal.setEnabled(true);
                binding.btnConfirmarFinal.setText("Confirmar cita");
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * METODO ANTIGUO: Solo creaba la cita sin pago.
     */
//    private void realizarReserva() {
//        String direccionEnvio = null;
//
//        // Validación: Si es domicilio, la dirección es obligatoria
//        if (esDomicilio) {
//            direccionEnvio = binding.etDireccionDomicilio.getText().toString().trim();
//            if (direccionEnvio.isEmpty()) {
//                binding.etDireccionDomicilio.setError("Ingrese su dirección exacta");
//                return;
//            }
//        }
//
//        // Crear el objeto Request (Usa las clases con @SerializedName que creamos)
//        CitaCreateRequest request = new CitaCreateRequest(idHorario, direccionEnvio);
//
//        // Bloquear botón para evitar doble clic
//        binding.btnConfirmarFinal.setEnabled(false);
//        binding.btnConfirmarFinal.setText("Procesando...");
//
//        // Llamada a la API
//        Call<ItemResponse<CitaResponse>> call = apiService.createCita(request);
//
//        call.enqueue(new Callback<ItemResponse<CitaResponse>>() {
//            @Override
//            public void onResponse(Call<ItemResponse<CitaResponse>> call, Response<ItemResponse<CitaResponse>> response) {
//                binding.btnConfirmarFinal.setEnabled(true);
//                binding.btnConfirmarFinal.setText("Confirmar cita");
//
//                if (response.isSuccessful() && response.body() != null) {
//                    // ÉXITO
//                    Toast.makeText(getContext(), "¡Cita reservada con éxito!", Toast.LENGTH_LONG).show();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("nombre_doctor", nombreDoctor);
//                    bundle.putString("fecha", fecha);
//                    bundle.putString("hora", hora);
//
//                    // Ubicación depende de la lógica
//                    String ubi = esDomicilio ? binding.etDireccionDomicilio.getText().toString() : direccionCentro;
//                    bundle.putString("ubicacion", ubi);
//
//                    // Precio y el QR que viene de la respuesta de la API
//                    bundle.putDouble("precio", precioReal); // O el precio que mostraste
//                    bundle.putString("codigo_qr_data", response.body().getData().getCodigoQr()); // <-- QR de la API
//                    try {
//                        Navigation.findNavController(requireView()).navigate(R.id.action_resumen_to_confirmada, bundle);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    // ERROR DEL SERVIDOR (Ej. 409 Horario Ocupado)
//                    String errorMsg = "Error al reservar";
//                    if (response.code() == 409) errorMsg = "El horario ya fue ocupado por otra persona.";
//                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ItemResponse<CitaResponse>> call, Throwable t) {
//                binding.btnConfirmarFinal.setEnabled(true);
//                binding.btnConfirmarFinal.setText("Confirmar cita");
//                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    // --- 6. API AUXILIARES ---

    private void cargarTiposPagos() {
        apiService.getTiposPago().enqueue(new Callback<ItemListResponse<TipoPagoResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ItemListResponse<TipoPagoResponse>> call,
                                   @NonNull Response<ItemListResponse<TipoPagoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tiposPago = response.body().getData();
                    Log.d("ResumenCita", "Tipos de pago cargados: " + tiposPago.size());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ItemListResponse<TipoPagoResponse>> call, @NonNull Throwable t) {
                Log.e("ResumenCita", "Error cargando tipos de pago");
            }
        });
    }
}