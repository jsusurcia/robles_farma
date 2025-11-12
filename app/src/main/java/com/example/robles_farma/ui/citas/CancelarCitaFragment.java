package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentCancelarCitaBinding;
import com.example.robles_farma.response.CancelarCitaResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CancelarCitaFragment extends Fragment {
    FragmentCancelarCitaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCancelarCitaBinding.inflate(inflater, container, false);

        // Recuperar los argumentos
        Bundle args = getArguments();
        if (args == null) return binding.getRoot();

        int idCita = args.getInt("idCita", 0);
        int idPersonal = args.getInt("idPersonal", 0);

        // Configurar el botón "Volver"
        binding.btnVolver.setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();
        });

        // Configurar el botón "Confirmar Cancelación"
        binding.btnConfirmarCancelacion.setOnClickListener(v -> {
            Log.d("CancelarCitaFragment", "ID de la cita: " + idCita);
            cancelarCita(idCita);
            Log.d("CancelarCitaFragment", "Si ves esto es pq ya ejecutó el método");
        });

        return binding.getRoot();
    }

    private void cancelarCita(int idCita) {
        ApiService apiService = RetrofitClient.createService();
        Call<CancelarCitaResponse> call = apiService.cancelarCita(idCita);
        call.enqueue(new Callback<CancelarCitaResponse>() {
            @Override
            public void onResponse(Call<CancelarCitaResponse> call, Response<CancelarCitaResponse> response) {
                if (response.isSuccessful()) {
                    CancelarCitaResponse cancelarCitaResponse = response.body();
                    // Si es respuesta exitosa, va a devolver un "data", si fuese error solo devuelve un "detail"
                    if (cancelarCitaResponse != null && cancelarCitaResponse.getData() != null) {
                        String message = cancelarCitaResponse.getMessage();
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(binding.getRoot()).popBackStack(R.id.navigation_citas, false);
                    } else {
                        String error = response.errorBody().toString();
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cancelar la cita: " + response.message(), Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonError = new JSONObject(response.errorBody().string());
                        String error = jsonError.getString("message");
                        Toast.makeText(getContext(), "Error al cancelar la cita: " + error, Toast.LENGTH_SHORT).show();
                    }catch (IOException | JSONException e){
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelarCitaResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error general de la API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}