package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
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
import com.example.robles_farma.ui.chat.ChatListFragment;

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
    private List<CitasPacienteData> listaCitasPasadas = new ArrayList<>();
    private LoginStorage loginStorage;
    PacienteResponse paciente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPasadasCitasBinding.inflate(inflater, container, false);
        loginStorage = new LoginStorage(getContext());
        paciente = loginStorage.getPaciente();
        adapter = new CitasRecyclerViewAdapter(listaCitasPasadas, getContext());

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
            public void onResponse(Call<CitasPacienteResponse> call, Response<CitasPacienteResponse> response) {

                if (response.isSuccessful()) {

                    listaCitasPasadas.clear();
                    CitasPacienteResponse citasResponse = response.body();

                    if (citasResponse != null && citasResponse.getData() != null && citasResponse.getData().length > 0) {

                        binding.recyclerViewPasadas.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);

                        for (CitasPacienteData c : citasResponse.getData()) {

                            if (c.getIdPersonal() != 0 && c.getNombrePersonal() != null) {
                                ChatListFragment.doctorNames.put(
                                        String.valueOf(c.getIdPersonal()),
                                        c.getNombrePersonal()
                                );
                            }

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
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    binding.recyclerViewPasadas.setVisibility(View.GONE);
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CitasPacienteResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error general de la API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                binding.recyclerViewPasadas.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
    }
}
