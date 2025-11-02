package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robles_farma.R;
import com.example.robles_farma.adapter.CitasRecyclerViewAdapter;
import com.example.robles_farma.databinding.FragmentPasadasCitasBinding;
import com.example.robles_farma.model.CitasData;

import java.util.ArrayList;
import java.util.List;

public class PasadasCitasFragment extends Fragment {
    private FragmentPasadasCitasBinding binding;
    private CitasRecyclerViewAdapter adapter;
    private List<CitasData> listaCitasPasadas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPasadasCitasBinding.inflate(inflater, container, false);
        adapter = new CitasRecyclerViewAdapter(listaCitasPasadas, getContext());

        binding.recyclerViewPasadas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPasadas.setAdapter(adapter);

        cargarCitasEjemplo();

        return binding.getRoot();
    }

    private void cargarCitasEjemplo() {
        listaCitasPasadas.add(new CitasData(
                "Dr. Atendida",
                "Cardiología",
                "15 Nov 2024 - 10:00 AM",
                "Consultorio 301",
                "Atendida"
        ));

        listaCitasPasadas.add(new CitasData(
                "Dra. Atendida",
                "Pediatría",
                "20 Nov 2024 - 3:00 PM",
                "Consultorio 205",
                "Atendida"
        ));

        adapter.notifyDataSetChanged();
    }
}