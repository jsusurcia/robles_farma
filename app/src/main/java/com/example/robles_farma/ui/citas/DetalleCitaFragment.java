package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentDetalleCitaBinding;

public class DetalleCitaFragment extends Fragment {
    FragmentDetalleCitaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetalleCitaBinding.inflate(inflater, container, false);

        // Recuperar los argumentos
        Bundle args = getArguments();
        if (args == null) return binding.getRoot();

        int idCita = args.getInt("idCita", 0);
        int idPersonal = args.getInt("idPersonal", 0);
        int idEspecialidad = args.getInt("idEspecialidad", 0);
        String doctorName = args.getString("doctorName", "");
        String specialty = args.getString("specialty", "");
        String date = args.getString("date", "");
        String hour = args.getString("hour", "");
        String location = args.getString("location", "Centro Médico");
        boolean enCentroMedico = args.getBoolean("enCentroMedico", false);

        //Lógica para ocultar el botón "Enviar Mensaje"
        if (!enCentroMedico) {
            binding.btnEditarUbicacion.setVisibility(View.GONE);
        }

        // Mostrar los detalles en el layout
        binding.tvNombreMedico.setText(doctorName);
        binding.tvEspecialidad.setText(specialty);
        binding.tvFecha.setText(date);
        binding.tvHora.setText(hour);
        binding.tvUbicacion.setText(location);

        // Configurar el botón "Enviar Mensaje"
        binding.btnEnviarMensaje.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Aquí se supone que va la lógica para abrir el chat", Toast.LENGTH_SHORT).show();
        });

        // Configurar el botón "Editar Ubicación
        binding.btnEditarUbicacion.setOnClickListener(v -> {
            redirigirFragmentEditarUbicacion(v, idCita, idPersonal);
        });

        // Configurar el botón "Cancelar"
        binding.btnCancelar.setOnClickListener(v -> {
            redirigirFragmentCancelar(v, idCita, idPersonal);
        });

        binding.btnReprogramar.setOnClickListener(v -> {
            redirigirFragmentReprogramar(v, idCita, idPersonal, idEspecialidad, doctorName, specialty, date, hour, location, enCentroMedico);
        });

        return binding.getRoot();
    }

    private void redirigirFragmentEditarUbicacion(View v, int idCita, int idPersonal) {
        Bundle argsEditarUbicacion = new Bundle();
        argsEditarUbicacion.putInt("idCita", idCita);
        argsEditarUbicacion.putInt("idPersonal", idPersonal);
        Navigation.findNavController(v).navigate(R.id.action_navigation_detalle_cita_to_navigation_editar_ubicacion_cita, argsEditarUbicacion);
    }

    private void redirigirFragmentCancelar(View v, int idCita, int idPersonal) {
        Bundle argsCancelar = new Bundle();
        argsCancelar.putInt("idCita", idCita);
        argsCancelar.putInt("idPersonal", idPersonal);
        Navigation.findNavController(v).navigate(R.id.action_navigation_detalle_cita_to_navigation_cancelar_cita, argsCancelar);
    }

    private void redirigirFragmentReprogramar(View v, int idCita, int idPersonal, int idEspecialidad, String doctorName, String specialty, String date, String hour, String location, boolean enCentroMedico) {
        Bundle argsReprogramar = new Bundle();
        argsReprogramar.putInt("idCita", idCita);
        argsReprogramar.putInt("idPersonal", idPersonal);
        argsReprogramar.putInt("idEspecialidad", idEspecialidad);
        argsReprogramar.putString("doctorName", doctorName);
        argsReprogramar.putString("specialty", specialty);
        argsReprogramar.putString("date", date);
        argsReprogramar.putString("hour", hour);
        argsReprogramar.putString("location", location);
        argsReprogramar.putBoolean("enCentroMedico", enCentroMedico);
        Navigation.findNavController(v).navigate(R.id.action_navigation_detalle_cita_to_navigation_reprogramar_cita, argsReprogramar);
    }
}