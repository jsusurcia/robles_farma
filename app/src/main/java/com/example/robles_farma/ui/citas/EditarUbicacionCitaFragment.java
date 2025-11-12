package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentEditarUbicacionCitaBinding;

public class EditarUbicacionCitaFragment extends Fragment {
    FragmentEditarUbicacionCitaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditarUbicacionCitaBinding.inflate(inflater, container, false);

        // Recuperar los argumentos
        Bundle args = getArguments();
        if (args == null) return binding.getRoot();

        int idCita = args.getInt("idCita", 0);
        int idPersonal = args.getInt("idPersonal", 0);

        return inflater.inflate(R.layout.fragment_editar_ubicacion_cita, container, false);
    }
}