package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.robles_farma.adapter.CitasRecyclerViewAdapter;
import com.example.robles_farma.databinding.FragmentPasadasCitasBinding;
import com.example.robles_farma.model.CitasPacienteData;
import com.example.robles_farma.response.CitasPacienteResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasadasCitasFragment extends Fragment {
    private FragmentPasadasCitasBinding binding;
    private CitasRecyclerViewAdapter adapter;
    private List<CitasPacienteData> listaCitasPasadas = new ArrayList<>();
    private LoginStorage loginStorage;
    PacienteResponse paciente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPasadasCitasBinding.inflate(inflater, container, false);
        loginStorage = new LoginStorage(getContext());
        paciente = loginStorage.getPaciente();
        adapter = new CitasRecyclerViewAdapter(listaCitasPasadas, getContext());

        binding.recyclerViewPasadas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPasadas.setAdapter(adapter);

        //Obtener el ID del paciente
        if (paciente != null) {
            int idPaciente = paciente.getIdPaciente();
            cargarPasadasCitas(idPaciente);
        }

        return binding.getRoot();
    }

    private void cargarPasadasCitas(int idPaciente) {
        ApiService apiService = RetrofitClient.createService();
        Call<CitasPacienteResponse> call = apiService.getCitasPasadas(idPaciente);
        call.enqueue(new Callback<CitasPacienteResponse>() {
            @Override
            public void onResponse(Call<CitasPacienteResponse> call, Response<CitasPacienteResponse> response) {
                if (response.isSuccessful()) {
                    listaCitasPasadas.clear();

                    CitasPacienteResponse citasResponse = response.body();
                    if (citasResponse != null && citasResponse.getData() != null && citasResponse.getData().length > 0) {
                        binding.recyclerViewPasadas.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);
                        listaCitasPasadas.addAll(Arrays.asList(citasResponse.getData()));
                        adapter.notifyDataSetChanged();
                    } else {
                        binding.recyclerViewPasadas.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al acceder a las citas: " + response.message(), Toast.LENGTH_SHORT).show();
                    try {
                        JSONObject jsonError = new JSONObject(response.errorBody().string());
                        String error = jsonError.getString("message");
                        Toast.makeText(getContext(), "Error al acceder a las citas: " + error, Toast.LENGTH_SHORT).show();
                        binding.recyclerViewPasadas.setVisibility(View.GONE);
                        binding.emptyView.setVisibility(View.VISIBLE);
                    }catch (IOException | JSONException e){
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<CitasPacienteResponse> call, Throwable t) {

            }
        });
    }

    private void cargarCitasEjemplo() {
        listaCitasPasadas.add(new CitasPacienteData(
                "Dr. Atendida",
                "Cardiología",
                "15 Nov 2024",
                "10:00",
                "Consultorio 301",
                "Atendida"
        ));

        listaCitasPasadas.add(new CitasPacienteData(
                "Dra. Atendida",
                "Pediatría",
                "20 Nov 2024",
                "14:00",
                "Consultorio 205",
                "Atendida"
        ));

        adapter.notifyDataSetChanged();
    }
}