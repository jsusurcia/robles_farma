package com.example.robles_farma.ui.citas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.robles_farma.databinding.FragmentEditarUbicacionCitaBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditarUbicacionCitaFragment extends Fragment {
    private FragmentEditarUbicacionCitaBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource;

    // Variables para almacenar los datos de la ubicación
    private int idCita;
    private int idPersonal;
    private Double latitud;
    private Double longitud;
    private String direccionTexto;
    private boolean esUbicacionGPS = false;

    // Launcher para solicitar permisos de ubicación
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                Boolean fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted) {
                    // Permiso de ubicación precisa concedido
                    obtenerUbicacionActual();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    // Solo permiso de ubicación aproximada concedido
                    obtenerUbicacionActual();
                } else {
                    // Permisos denegados
                    Toast.makeText(requireContext(),
                            "Se necesitan permisos de ubicación para usar esta función",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditarUbicacionCitaBinding.inflate(inflater, container, false);

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        cancellationTokenSource = new CancellationTokenSource();

        // Recuperar los argumentos
        Bundle args = getArguments();
        if (args != null) {
            idCita = args.getInt("idCita", 0);
            idPersonal = args.getInt("idPersonal", 0);
        }

        configurarListeners();

        return binding.getRoot();
    }

    private void configurarListeners() {
        // Listener para el botón de usar ubicación actual
        binding.btnUsarUbicacionActual.setOnClickListener(v -> {
            verificarYSolicitarPermisos();
        });

        // Listener para el botón de guardar
        binding.btnGuardar.setOnClickListener(v -> {
            guardarUbicacion();
        });

        // Listener para el botón de cancelar
        binding.btnCancelar.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    /**
     * Verifica si los permisos de ubicación están concedidos
     * Si no están concedidos, los solicita
     */
    private void verificarYSolicitarPermisos() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Los permisos ya están concedidos
            obtenerUbicacionActual();
        } else {
            // Solicitar permisos
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    /**
     * Obtiene la ubicación actual del dispositivo usando GPS
     * Similar a como lo hace Rappi o WhatsApp
     */
    private void obtenerUbicacionActual() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Mostrar indicador de carga
        binding.btnUsarUbicacionActual.setEnabled(false);
        binding.btnUsarUbicacionActual.setText("Obteniendo ubicación...");

        // Obtener la ubicación actual con alta precisión
        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.getToken()
        ).addOnSuccessListener(location -> {
            if (location != null) {
                // Guardar las coordenadas
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                esUbicacionGPS = true;

                // Obtener la dirección a partir de las coordenadas (Geocoding inverso)
                obtenerDireccionDesdeCoordenadasAsync(location);

                // Actualizar UI
                binding.btnUsarUbicacionActual.setText("✓ Ubicación obtenida");
                binding.btnUsarUbicacionActual.setEnabled(true);

                Toast.makeText(requireContext(),
                        "Ubicación capturada correctamente",
                        Toast.LENGTH_SHORT).show();
            } else {
                // No se pudo obtener la ubicación
                binding.btnUsarUbicacionActual.setText("Usar ubicación actual");
                binding.btnUsarUbicacionActual.setEnabled(true);
                Toast.makeText(requireContext(),
                        "No se pudo obtener la ubicación. Intenta de nuevo",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            binding.btnUsarUbicacionActual.setText("Usar ubicación actual");
            binding.btnUsarUbicacionActual.setEnabled(true);
            Toast.makeText(requireContext(),
                    "Error al obtener ubicación: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Convierte coordenadas GPS a una dirección legible
     * (Geocoding inverso)
     */
    private void obtenerDireccionDesdeCoordenadasAsync(Location location) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1
                );

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder direccion = new StringBuilder();

                    // Construir la dirección
                    if (address.getThoroughfare() != null) {
                        direccion.append(address.getThoroughfare());
                    }
                    if (address.getSubThoroughfare() != null) {
                        direccion.append(" ").append(address.getSubThoroughfare());
                    }
                    if (address.getLocality() != null) {
                        if (direccion.length() > 0) direccion.append(", ");
                        direccion.append(address.getLocality());
                    }
                    if (address.getAdminArea() != null) {
                        if (direccion.length() > 0) direccion.append(", ");
                        direccion.append(address.getAdminArea());
                    }

                    String direccionFinal = direccion.toString();
                    if (direccionFinal.isEmpty()) {
                        direccionFinal = String.format(Locale.getDefault(),
                                "Lat: %.6f, Lon: %.6f",
                                location.getLatitude(),
                                location.getLongitude());
                    }

                    direccionTexto = direccionFinal;

                    // Actualizar UI en el hilo principal
                    String finalDireccion = direccionFinal;
                    requireActivity().runOnUiThread(() -> {
                        binding.editTextUbicacion.setText(finalDireccion);
                        binding.editTextUbicacion.setEnabled(false);
                    });
                }
            } catch (IOException e) {
                // Si falla el geocoding, usar las coordenadas
                String coordenadas = String.format(Locale.getDefault(),
                        "Lat: %.6f, Lon: %.6f",
                        location.getLatitude(),
                        location.getLongitude());
                direccionTexto = coordenadas;

                requireActivity().runOnUiThread(() -> {
                    binding.editTextUbicacion.setText(coordenadas);
                    binding.editTextUbicacion.setEnabled(false);
                });
            }
        }).start();
    }

    /**
     * Valida y guarda la ubicación
     * Por ahora solo muestra los datos, pero prepara todo para consumir la API
     */
    private void guardarUbicacion() {
        // Obtener la dirección del campo de texto si no se usó GPS
        if (!esUbicacionGPS) {
            String direccionManual = binding.editTextUbicacion.getText() != null ?
                    binding.editTextUbicacion.getText().toString().trim() : "";

            if (direccionManual.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Por favor ingresa una ubicación o usa tu ubicación actual",
                        Toast.LENGTH_SHORT).show();
                binding.inputLayoutUbicacion.setError("La ubicación es requerida");
                return;
            }

            direccionTexto = direccionManual;
            latitud = null;
            longitud = null;
        }

        // Limpiar errores
        binding.inputLayoutUbicacion.setError(null);

        // Preparar los datos para enviar a la API
        UbicacionData ubicacionData = new UbicacionData(
                idCita,
                idPersonal,
                direccionTexto,
                latitud,
                longitud,
                esUbicacionGPS
        );

        // Por ahora, mostrar los datos que se enviarían a la API
        mostrarDatosUbicacion(ubicacionData);

        // TODO: Cuando tengas la API lista, reemplaza mostrarDatosUbicacion() con:
        // enviarUbicacionAPI(ubicacionData);
    }

    /**
     * Muestra los datos de ubicación capturados
     * Este método es temporal, será reemplazado por la llamada a la API
     */
    private void mostrarDatosUbicacion(UbicacionData data) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("📍 DATOS DE UBICACIÓN CAPTURADOS\n\n");
        mensaje.append("ID Cita: ").append(data.idCita).append("\n");
        mensaje.append("ID Personal: ").append(data.idPersonal).append("\n");
        mensaje.append("Dirección: ").append(data.direccion).append("\n\n");

        if (data.esGPS) {
            mensaje.append("🛰️ Ubicación GPS:\n");
            mensaje.append("Latitud: ").append(String.format(Locale.getDefault(), "%.6f", data.latitud)).append("\n");
            mensaje.append("Longitud: ").append(String.format(Locale.getDefault(), "%.6f", data.longitud)).append("\n");
        } else {
            mensaje.append("✏️ Ubicación ingresada manualmente\n");
        }

        mensaje.append("\n✅ Datos listos para enviar a la API");

        Toast.makeText(requireContext(), mensaje.toString(), Toast.LENGTH_LONG).show();

        // Log para debugging
        android.util.Log.d("UbicacionCita", "Datos preparados: " + data.toString());
    }

    /**
     * Método preparado para cuando tengas la API lista
     * Descomenta y modifica según tu API
     */
    /*
    private void enviarUbicacionAPI(UbicacionData data) {
        // Ejemplo de cómo consumir la API con Retrofit o tu cliente HTTP preferido

        // Mostrar loading
        binding.btnGuardar.setEnabled(false);
        binding.btnGuardar.setText("Guardando...");

        // Crear el JSON para enviar
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("idCita", data.idCita);
            jsonBody.put("idPersonal", data.idPersonal);
            jsonBody.put("direccion", data.direccion);

            if (data.esGPS) {
                jsonBody.put("latitud", data.latitud);
                jsonBody.put("longitud", data.longitud);
                jsonBody.put("tipoUbicacion", "GPS");
            } else {
                jsonBody.put("tipoUbicacion", "MANUAL");
            }

            // Llamada a tu API
            // apiService.actualizarUbicacionCita(jsonBody)
            //     .enqueue(new Callback<Response>() { ... });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error al preparar datos", Toast.LENGTH_SHORT).show();
            binding.btnGuardar.setEnabled(true);
            binding.btnGuardar.setText("Guardar");
        }
    }
    */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancelar cualquier operación de ubicación pendiente
        if (cancellationTokenSource != null) {
            cancellationTokenSource.cancel();
        }
        binding = null;
    }

    /**
     * Clase interna para estructurar los datos de ubicación
     * Lista para ser enviada a la API
     */
    private static class UbicacionData {
        int idCita;
        int idPersonal;
        String direccion;
        Double latitud;
        Double longitud;
        boolean esGPS;

        UbicacionData(int idCita, int idPersonal, String direccion,
                      Double latitud, Double longitud, boolean esGPS) {
            this.idCita = idCita;
            this.idPersonal = idPersonal;
            this.direccion = direccion;
            this.latitud = latitud;
            this.longitud = longitud;
            this.esGPS = esGPS;
        }

        @Override
        public String toString() {
            return "UbicacionData{" +
                    "idCita=" + idCita +
                    ", idPersonal=" + idPersonal +
                    ", direccion='" + direccion + '\'' +
                    ", latitud=" + latitud +
                    ", longitud=" + longitud +
                    ", esGPS=" + esGPS +
                    '}';
        }
    }
}