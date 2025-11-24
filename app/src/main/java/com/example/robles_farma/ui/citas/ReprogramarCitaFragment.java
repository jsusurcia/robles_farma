package com.example.robles_farma.ui.citas;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.robles_farma.R;
//import com.example.robles_farma.adapter.BloqueHorarioDisponibleRecyclerViewAdapter;
import com.example.robles_farma.adapter.BloqueHorarioDisponibleRecyclerViewAdapter;
import com.example.robles_farma.databinding.FragmentReprogramarCitaBinding;
import com.example.robles_farma.model.BloqueHorarioDisponibleData;
import com.example.robles_farma.model.HorarioEspecialidadData;
import com.example.robles_farma.response.HorarioEspecialidadResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReprogramarCitaFragment extends Fragment {
    private FragmentReprogramarCitaBinding binding;
    private BloqueHorarioDisponibleRecyclerViewAdapter adapter;
    private List<BloqueHorarioDisponibleData> listaHorarios = new ArrayList<>();
    private LoginStorage loginStorage;
    private PacienteResponse paciente;

    // Variables para almacenar datos de la cita
    private int idCita;
    private int idPersonal;
    private int idEspecialidad;
    private String doctorName;
    private String specialty;
    private String date;
    private String hour;
    private String location;
    private boolean enCentroMedico;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReprogramarCitaBinding.inflate(inflater, container, false);
        loginStorage = new LoginStorage(getContext());
        paciente = loginStorage.getPaciente();

        // Configurar RecyclerView con GridLayoutManager (2 columnas para chips)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerViewHorarios.setLayoutManager(gridLayoutManager);

        adapter = new BloqueHorarioDisponibleRecyclerViewAdapter(listaHorarios, getContext());
        binding.recyclerViewHorarios.setAdapter(adapter);

        // Configurar calendario picker
        binding.editTextFecha.setOnClickListener(v -> {
            mostrarCalendario();
        });
        
        // Recuperar los argumentos
        Bundle args = getArguments();
        if (args != null) {
            idCita = args.getInt("idCita", 0);
            idPersonal = args.getInt("idPersonal", 0);
            idEspecialidad = args.getInt("idEspecialidad", 0);
            doctorName = args.getString("doctorName", "");
            specialty = args.getString("specialty", "");
            date = args.getString("date", "");
            hour = args.getString("hour", "");
            location = args.getString("location", "Centro Médico");
            enCentroMedico = args.getBoolean("enCentroMedico", false);

            mostrarInformacionActual();
        }

        return binding.getRoot();
    }

    private void mostrarCalendario() {
        final Calendar calendar = Calendar.getInstance();
        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (DatePicker view, int year, int month, int dayOfMonth) -> {
            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(year, month, dayOfMonth);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaFormateada = format.format(fechaSeleccionada.getTime());
            binding.editTextFecha.setText(fechaFormateada);

            cargarHorarios(idEspecialidad, fechaFormateada, enCentroMedico);
        }, anio, mes, dia);

        // Evitar que se seleccionen fechas pasadas
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    private void mostrarInformacionActual() {
        if (binding == null) return;

        binding.textCurrentFecha.setText(date != null ? date : "");
        binding.textCurrentHora.setText(hour != null ? hour : "");
        binding.textCurrentUbicacion.setText(location != null ? location : "");
        binding.textCurrentDoctor.setText(doctorName != null ? doctorName : "");
    }

    private void cargarHorarios(int idEspecialidad, String date, boolean enCentroMedico) {
        // Mostrar estado de carga
        mostrarEstadoCargando();

        ApiService apiService = RetrofitClient.createService();
        Call<HorarioEspecialidadResponse> call = apiService.getHorariosDisponibles(
                idEspecialidad,
                date,
                enCentroMedico,
                loginStorage.getToken()
        );
        call.enqueue(new Callback<HorarioEspecialidadResponse>() {
            @Override
            public void onResponse(Call<HorarioEspecialidadResponse> call, Response<HorarioEspecialidadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HorarioEspecialidadResponse horarioResponse = response.body();

                    Log.d("API_SUCCESS", "Status: " + horarioResponse.getStatus());
                    Log.d("API_SUCCESS", "Message: " + horarioResponse.getMessage());

                    if ("success".equals(horarioResponse.getStatus())) {
                        listaHorarios.clear();
                        HorarioEspecialidadData[] data = horarioResponse.getData();

                        if (data != null && data.length > 0) {
                            // Recorrer cada médico y sus horarios
                            for (HorarioEspecialidadData medico : data) {
                                Log.d("API_SUCCESS", "Médico: " + medico.getNombreCompleto());

                                BloqueHorarioDisponibleData[] horarios = medico.getHorarios();
                                if (horarios != null && horarios.length > 0) {
                                    listaHorarios.addAll(Arrays.asList(horarios));
                                    Log.d("API_SUCCESS", "Horarios agregados: " + horarios.length);
                                }
                            }
                        }

                        // Actualizar UI según si hay datos o no
                        if (listaHorarios.isEmpty()) {
                            Log.d("API_SUCCESS", "No hay horarios disponibles");
                            mostrarEstadoVacio();
                        } else {
                            Log.d("API_SUCCESS", "Total horarios: " + listaHorarios.size());
                            mostrarEstadoConDatos();
                        }
                    } else {
                        Log.e("API_ERROR", "Status no es success: " + horarioResponse.getStatus());
                        mostrarEstadoVacio();
                    }
                } else {
                    Log.e("API_ERROR", "Response no exitoso. Code: " + response.code());
                    mostrarEstadoVacio();
                }
            }

            @Override
            public void onFailure(Call<HorarioEspecialidadResponse> call, Throwable t) {
                Log.e("API_FAILURE", "Error en la llamada a la API: " + t.getMessage());
                t.printStackTrace();
                mostrarEstadoError();
            }
        });
    }

    private void mostrarEstadoCargando() {
        if (binding == null) return;

        binding.layoutCargandoHorarios.setVisibility(View.VISIBLE);
        binding.layoutHorariosContainer.setVisibility(View.GONE);
        binding.layoutNoHorarios.setVisibility(View.GONE);
    }

    private void mostrarEstadoConDatos() {
        if (binding == null) return;

        binding.layoutCargandoHorarios.setVisibility(View.GONE);
        binding.layoutHorariosContainer.setVisibility(View.VISIBLE);
        binding.layoutNoHorarios.setVisibility(View.GONE);
        //adapter.notifyDataSetChanged();

        Log.d("UI_STATE", "Mostrando estado: CON DATOS (" + listaHorarios.size() + " horarios)");
    }

    private void mostrarEstadoVacio() {
        if (binding == null) return;

        binding.layoutCargandoHorarios.setVisibility(View.GONE);
        binding.layoutHorariosContainer.setVisibility(View.GONE);
        binding.layoutNoHorarios.setVisibility(View.VISIBLE);
    }

    private void mostrarEstadoError() {
        if (binding == null) return;

        binding.layoutCargandoHorarios.setVisibility(View.GONE);
        binding.layoutHorariosContainer.setVisibility(View.GONE);
        binding.layoutNoHorarios.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}