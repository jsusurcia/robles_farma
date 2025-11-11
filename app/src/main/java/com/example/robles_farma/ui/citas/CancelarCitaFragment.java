package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentCancelarCitaBinding;

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

        Log.d("CancelarCitaFragment", "ID de la cita: " + idCita);
        Log.d("CancelarCitaFragment", "ID del personal: " + idPersonal);

        return binding.getRoot();
    }
}