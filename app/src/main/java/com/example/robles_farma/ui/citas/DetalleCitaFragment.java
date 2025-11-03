package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentDetalleCitaBinding;

public class DetalleCitaFragment extends Fragment {
    FragmentDetalleCitaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetalleCitaBinding.inflate(inflater, container, false);

        Bundle args = getArguments();
        if (args == null) return binding.getRoot();

        String doctorName = args.getString("doctorName", "");
        String specialty = args.getString("specialty", "");
        String date = args.getString("date", "");
        String hour = args.getString("hour", "");
        String location = args.getString("location", "");

        binding.tvNombreMedico.setText(doctorName);
        binding.tvEspecialidad.setText(specialty);
        binding.tvFecha.setText(date);
        binding.tvHora.setText(hour);
        binding.tvUbicacion.setText(location);

        return binding.getRoot();
    }
}