package com.example.robles_farma.ui.citas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.robles_farma.adapter.CitasRecyclerViewAdapter;
import com.example.robles_farma.databinding.FragmentPasadasCitasBinding;
import com.example.robles_farma.model.CitasPacienteData;
import com.example.robles_farma.response.CitasPacienteResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;
// ✅ ELIMINADO: import com.example.robles_farma.ui.chat.ChatListFragment; (Ya no se necesita)

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasadasCitasFragment extends Fragment {

    private FragmentPasadasCitasBinding binding;
    private CitasRecyclerViewAdapter adapter;
    private final List<CitasPacienteData> listaCitasPasadas = new ArrayList<>();
    private LoginStorage loginStorage;
    private PacienteResponse paciente;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPasadasCitasBinding.inflate(inflater, container, false);
        loginStorage = new LoginStorage(requireContext());
        paciente = loginStorage.getPaciente();
        adapter = new CitasRecyclerViewAdapter(listaCitasPasadas, requireContext(), true);

        binding.recyclerViewPasadas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPasadas.setAdapter(adapter);

        if (paciente != null) {
            cargarPasadasCitas(paciente.getIdPaciente());
        }

        return binding.getRoot();
    }

    private void cargarPasadasCitas(int idPaciente) {

        ApiService apiService = RetrofitClient.createService();
        Call<CitasPacienteResponse> call = apiService.getCitasPasadas(idPaciente);

        call.enqueue(new Callback<CitasPacienteResponse>() {
            @Override
            public void onResponse(@NonNull Call<CitasPacienteResponse> call, @NonNull Response<CitasPacienteResponse> response) {

                if (response.isSuccessful()) {

                    listaCitasPasadas.clear();
                    CitasPacienteResponse citasResponse = response.body();

                    if (citasResponse != null && citasResponse.getData() != null && citasResponse.getData().length > 0) {

                        binding.recyclerViewPasadas.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);

                        for (CitasPacienteData c : citasResponse.getData()) {
                            // ✅ ELIMINADO: Ya no guardamos manualmente los nombres en doctorNames
                            listaCitasPasadas.add(c);
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        binding.recyclerViewPasadas.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }

                } else {
                    String errorMessage = "Error desconocido";

                    if (response.errorBody() != null) {
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            if (jsonError.has("detail")) {
                                errorMessage = jsonError.getString("detail");
                            } else {
                                errorMessage = response.message();
                            }
                        } catch (IOException | JSONException e) {
                            errorMessage = "Error al procesar la respuesta.";
                        }
                    }

                    Log.e("CitasError", errorMessage);
                    if (getContext() != null) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    binding.recyclerViewPasadas.setVisibility(View.GONE);
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CitasPacienteResponse> call, @NonNull Throwable t) {
                Log.e("CitasError", "Error general de la API (Pasadas): " + t.getMessage());
                if (binding != null) { // Check simple para evitar crash si el fragmento se cerró
                    binding.recyclerViewPasadas.setVisibility(View.GONE);
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}