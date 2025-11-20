package com.example.robles_farma.ui.citas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.robles_farma.R;
import com.example.robles_farma.adapter.HorariosAdapter;
import com.example.robles_farma.databinding.FragmentReservarCitaBinding;
import com.example.robles_farma.model.HorarioItem;
import com.example.robles_farma.response.ItemListResponse;
import com.example.robles_farma.response.MedicoConHorariosResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservarCitaFragment extends Fragment {

    private FragmentReservarCitaBinding binding;

    // Componentes visuales
    private CalendarView calendarView;
    private RecyclerView rvHorarios;
    private TextView tvEspecialidadTitulo;

    // Variables de datos
    private String especialidadNombre;
    private int especialidadId = -1; // ID para la API
    private boolean enCentroMedico = true; // Toggle

    // Retrofit y Adapter
    private ApiService apiService;
    private HorariosAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservarCitaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Vincular vistas
        calendarView = binding.calendarView;
        rvHorarios = binding.rvHorariosDisponibles;
        tvEspecialidadTitulo = binding.tvEspecialidadTitulo;

        // 2. Iniciar API
        apiService = RetrofitClient.createService();

        // 3. Configurar RecyclerView
        setupRecyclerView();

        // 4. Recibir Datos (ID y Nombre)
        recibirDatos();

        // 5. Configurar Calendario
        configurarCalendario();

        // 6. Configurar Toggle (Centro Médico vs Domicilio)
        configurarToggle();
    }

    private void setupRecyclerView() {
        adapter = new HorariosAdapter();
        adapter.setOnItemClickListener(item -> {
            // TODO: Guardar horario seleccionado y pasar a confirmar
            Toast.makeText(getContext(), "Hora: " + item.horario.getHora_inicio() + " con " + item.nombreDoctor, Toast.LENGTH_SHORT).show();
        });
        rvHorarios.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Grid 2 columnas
        //Con esto se desactiva el scroll interno
        rvHorarios.setNestedScrollingEnabled(false);
        //Permite que el recycler vio se expanda libremente
        rvHorarios.setHasFixedSize(false);
        rvHorarios.setAdapter(adapter);

    }

    private void recibirDatos() {
        if (getArguments() != null) {
            especialidadNombre = getArguments().getString("nombre_especialidad", "Especialidad");
            especialidadId = getArguments().getInt("id_especialidad", -1);

            tvEspecialidadTitulo.setText(especialidadNombre);
        }
    }

    private void configurarCalendario() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);

        try {
            calendarView.setDate(today);
            calendarView.setMinimumDate(today);
        } catch (Exception e) { e.printStackTrace(); }

        // Borde verde para "hoy"
        List<EventDay> events = new ArrayList<>();
        events.add(new EventDay(today, R.drawable.today_border));
        calendarView.setEvents(events);

        // Listener de Clic en Fecha
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar fechaSeleccionada = eventDay.getCalendar();
                cargarHorarios(fechaSeleccionada);
            }
        });
        cargarHorarios(today);
    }

    private void configurarToggle() {
        binding.toggleTipoVisita.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_en_centro) enCentroMedico = true;
                else if (checkedId == R.id.btn_a_domicilio) enCentroMedico = false;

                // Si ya hay fecha seleccionada, recargar
                if (calendarView.getFirstSelectedDate() != null) {
                    cargarHorarios(calendarView.getFirstSelectedDate());
                }
            }
        });
    }

    private void cargarHorarios(Calendar fecha) {
        if (especialidadId == -1) {
            Toast.makeText(getContext(), "Error: Especialidad no identificada", Toast.LENGTH_SHORT).show();
            return;
        }

        // Formatear Fecha
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaString = format.format(fecha.getTime());

        // Llamada a la API
        Call<ItemListResponse<MedicoConHorariosResponse>> call = apiService.getHorariosPorEspecialidad(
                especialidadId,
                fechaString,
                enCentroMedico
        );

        call.enqueue(new Callback<ItemListResponse<MedicoConHorariosResponse>>() {
            @Override
            public void onResponse(Call<ItemListResponse<MedicoConHorariosResponse>> call, Response<ItemListResponse<MedicoConHorariosResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<MedicoConHorariosResponse> medicos = response.body().getData();
                    List<HorariosAdapter.HorarioDisplay> listaPlana = new ArrayList<>();

                    // Aplanar datos: De (Médico -> Lista Horas) a (Lista Plana Visual)
                    if (medicos != null) {
                        for (MedicoConHorariosResponse medico : medicos) {
                            for (HorarioItem h : medico.getHorarios()) {
                                listaPlana.add(new HorariosAdapter.HorarioDisplay(h, medico.getNombre_completo()));
                            }
                        }
                    }

                    adapter.setDatos(listaPlana); // Actualizar UI

                    if (listaPlana.isEmpty()) {
                        Toast.makeText(getContext(), "No hay horarios disponibles.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "Error al cargar horarios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ItemListResponse<MedicoConHorariosResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}