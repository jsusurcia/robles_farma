package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.robles_farma.R;
import com.example.robles_farma.adapter.CitasRecyclerViewAdapter;
import com.example.robles_farma.databinding.FragmentProximasCitasBinding;
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
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProximasCitasFragment extends Fragment {
    private FragmentProximasCitasBinding binding;
    private CitasRecyclerViewAdapter adapter;
    private List<CitasPacienteData> listaProxCitas = new ArrayList<>();
    private LoginStorage loginStorage;
    PacienteResponse paciente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProximasCitasBinding.inflate(inflater, container, false);
        loginStorage = new LoginStorage(getContext());
        paciente = loginStorage.getPaciente();
        adapter = new CitasRecyclerViewAdapter(listaProxCitas, getContext());

        binding.recyclerViewProximas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProximas.setAdapter(adapter);

        if (paciente != null) {
            cargarProximasCitas(paciente.getIdPaciente());
        }

        binding.btnReservarCita.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(ProximasCitasFragment.this);
            navController.navigate(R.id.navigation_home);
        });

        return binding.getRoot();
    }

    private void cargarProximasCitas(int idPaciente) {

        ApiService apiService = RetrofitClient.createService();
        Call<CitasPacienteResponse> call = apiService.getCitasProximas(idPaciente);

        call.enqueue(new Callback<CitasPacienteResponse>() {
            @Override
            public void onResponse(Call<CitasPacienteResponse> call, Response<CitasPacienteResponse> response) {

                if (response.isSuccessful()) {

                    listaProxCitas.clear();
                    CitasPacienteResponse citasResponse = response.body();

                    if (citasResponse != null && citasResponse.getData() != null && citasResponse.getData().length > 0) {

                        binding.recyclerViewProximas.setVisibility(View.VISIBLE);
                        binding.emptyView.setVisibility(View.GONE);

                        CitasPacienteData[] citas = citasResponse.getData();

                        for (CitasPacienteData c : citas) {

                            // 💾 Guardar nombre de doctor para chats
                            if (c.getIdPersonal() != 0 && c.getNombrePersonal() != null) {
                                ChatListFragment.doctorNames.put(
                                        String.valueOf(c.getIdPersonal()),
                                        c.getNombrePersonal()
                                );
                            }

                            listaProxCitas.add(c);
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        binding.recyclerViewProximas.setVisibility(View.GONE);
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

                    Log.e("CitasError", "Error API: " + errorMessage);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    binding.recyclerViewProximas.setVisibility(View.GONE);
                    binding.emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<CitasPacienteResponse> call, Throwable t) {
                Log.e("CitasError", "Error general de la API (Proximas): " + t.getMessage());
                binding.recyclerViewProximas.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

}
