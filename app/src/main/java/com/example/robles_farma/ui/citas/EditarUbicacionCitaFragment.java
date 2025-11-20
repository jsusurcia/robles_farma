package com.example.robles_farma.ui.citas;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.Navigation;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentEditarUbicacionCitaBinding;
import com.example.robles_farma.request.EditarUbicacionCitaRequest;
import com.example.robles_farma.response.EditarUbicacionCitaResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarUbicacionCitaFragment extends Fragment {
    private FragmentEditarUbicacionCitaBinding binding;

    // Variables para la ubicación GPS
    private FusedLocationProviderClient fusedLocationClient;
    private CancellationTokenSource cancellationTokenSource;

    // Variables para guardar datos
    private int idCita;
    private int idPersonal;
    private Double latitud;
    private Double longitud;
    private String direccionTexto;
    private boolean esUbicacionGPS = false;

    // Configuración para manejar los permisos de ubicación
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
                Boolean fineLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                if (fineLocationGranted != null && fineLocationGranted) {
                    // Permiso para la ubicación precisa
                    obtenerUbicacionActual();
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    // Permiso para la ubicación aproximada
                    obtenerUbicacionActual();
                } else {
                    // Permisos denegados
                    Toast.makeText(requireContext(), "Se necesitan permisos de ubicación para usar esta función", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

    // Metodo "global" para configurar los eventos "click" de los botones
    private void configurarListeners() {
        // Configurar botón "Usar ubicación actual"
        binding.btnUsarUbicacionActual.setOnClickListener(v -> {
            verificarYSolicitarPermisos();
        });

        // Configurar botón "Guardar"
        binding.btnGuardar.setOnClickListener(v -> {
            guardarUbicacion();
        });

        // Configurar botón "Cancelar"
        binding.btnCancelar.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    // Metodo para verificar y solicitar los permisos de ubicacion
    private void verificarYSolicitarPermisos() {
        // Revisar si ya tenemos permisos
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permisos concedidos
            obtenerUbicacionActual();
        } else {
            // Solicitar permisos
            requestPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    // Metodo para obtener la ubicación GPS actual
    private void obtenerUbicacionActual() {
        // Volver a verificar si se tienen permisos
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Mostrar indicador de carga en el botón
        binding.btnUsarUbicacionActual.setEnabled(false);
        binding.btnUsarUbicacionActual.setText("Obteniendo ubicación...");

        // Obtener la ubicación precisa actual
        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.getToken()
        ).addOnSuccessListener(location -> {
            if (location != null) {
                // Guardar las coordenadas
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                esUbicacionGPS = true;

                // Convertir las coordenadas a una dirección legible
                obtenerDireccionDesdeCoordenadasAsync(location);

                // Actualizar el botón
                binding.btnUsarUbicacionActual.setText("✓ Ubicación obtenida");
                binding.btnUsarUbicacionActual.setEnabled(true);

                Toast.makeText(requireContext(), "Ubicación capturada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                // No se pudo obtener la ubicación
                binding.btnUsarUbicacionActual.setText("Usar ubicación actual");
                binding.btnUsarUbicacionActual.setEnabled(true);
                Toast.makeText(requireContext(), "No se pudo obtener la ubicación. Intenta de nuevo", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            binding.btnUsarUbicacionActual.setText("Usar ubicación actual");
            binding.btnUsarUbicacionActual.setEnabled(true);
            Toast.makeText(requireContext(), "Error al obtener ubicación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Metodo para convertir las coordenadas a una dirección legible (Geocoding inverso)
    private void obtenerDireccionDesdeCoordenadasAsync(Location location) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

                // Obtener direcciones a partir de las coordenadas, limitandolas a 1 resultado
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder direccion = new StringBuilder();

                    // Construir la dirección
                    if (address.getThoroughfare() != null) {
                        // Calle
                        direccion.append(address.getThoroughfare());
                    }
                    if (address.getSubThoroughfare() != null) {
                        // Número
                        direccion.append(" ").append(address.getSubThoroughfare());
                    }
                    if (address.getLocality() != null) {
                        // Ciudad
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

                    // Mostrar la dirección obtenida en el campo de texto
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

    // Metodo para guardar la ubicacion (momentaneamente, hasta que esté la API)
    private void guardarUbicacion() {
        // Obtener la dirección del campo de texto si no se usó GPS
        if (!esUbicacionGPS) {
            String direccionManual = binding.editTextUbicacion.getText() != null ?
                    binding.editTextUbicacion.getText().toString().trim() : "";

            // Validar que el campo no esté vacio
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

        // Mostrar los datos obtenidos
        mostrarDatosUbicacion();
        editaUbicacion();
    }

    private void editaUbicacion() {
        ApiService apiService = RetrofitClient.createService();
        Call<EditarUbicacionCitaResponse> call = apiService.editarUbicacionCita(idCita, new EditarUbicacionCitaRequest(direccionTexto));
        call.enqueue(new Callback<EditarUbicacionCitaResponse>() {
            @Override
            public void onResponse(Call<EditarUbicacionCitaResponse> call, Response<EditarUbicacionCitaResponse> response) {
                if (response.isSuccessful()) {
                    EditarUbicacionCitaResponse editarUbicacionCitaResponse = response.body();
                    if (editarUbicacionCitaResponse != null && editarUbicacionCitaResponse.getData() != null) {
                        String message = editarUbicacionCitaResponse.getMessage();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(binding.getRoot()).popBackStack(R.id.navigation_citas, false);
                    } else {
                        String error = response.errorBody().toString();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al editar la ubicación de la cita: " + response.message(), Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonError = new JSONObject(response.errorBody().string());
                        String error = jsonError.getString("message");
                        Toast.makeText(getContext(), "Error al editar la ubicación de la cita: " + error, Toast.LENGTH_SHORT).show();
                    }catch (IOException | JSONException e){
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<EditarUbicacionCitaResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error general de la API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatosUbicacion() {
        Log.d("UbicacionCita", "Mostrando datos...");
        Log.d("UbicacionCita", "ID Cita: " + idCita);
        Log.d("UbicacionCita", "ID Personal: " + idPersonal);
        Log.d("UbicacionCita", "Dirección: " + direccionTexto);

        if (esUbicacionGPS) {
            Log.d("UbicacionCita", "Latitud: " + latitud);
            Log.d("UbicacionCita", "Longitud: " + longitud);
        } else {
            Log.d("UbicacionCita", "Dirección ingresada manualmente: " + direccionTexto);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancelar cualquier operación de ubicación pendiente
        if (cancellationTokenSource != null) {
            cancellationTokenSource.cancel();
        }
        binding = null;
    }
}