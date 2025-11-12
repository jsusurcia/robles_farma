package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentReprogramarCitaBinding;

public class ReprogramarCitaFragment extends Fragment {
    FragmentReprogramarCitaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReprogramarCitaBinding.inflate(inflater, container, false);

        // Recuperar los argumentos
        Bundle args = getArguments();
        if (args == null) return binding.getRoot();

        int idCita = args.getInt("idCita", 0);
        int idPersonal = args.getInt("idPersonal", 0);
        String doctorName = args.getString("doctorName", "");
        String specialty = args.getString("specialty", "");
        String date = args.getString("date", "");
        String hour = args.getString("hour", "");
        String location = args.getString("location", "Centro Médico");

        return binding.getRoot();
    }
}