package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robles_farma.R;
import com.example.robles_farma.adapter.CitasRecyclerViewAdapter;
import com.example.robles_farma.databinding.FragmentProximasCitasBinding;
import com.example.robles_farma.model.CitasData;

import java.util.ArrayList;
import java.util.List;

public class ProximasCitasFragment extends Fragment {
    private FragmentProximasCitasBinding binding;
    private CitasRecyclerViewAdapter adapter;
    private List<CitasData> listaProxCitas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProximasCitasBinding.inflate(inflater, container, false);
        adapter = new CitasRecyclerViewAdapter(listaProxCitas, getContext());

        binding.recyclerViewProximas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProximas.setAdapter(adapter);

        cargarCitasEjemplo();

        return binding.getRoot();
    }

    private void cargarCitasEjemplo() {
        listaProxCitas.add(new CitasData(
                "Dr. Juan Pérez",
                "Cardiología",
                "15 Nov 2024 - 10:00 AM",
                "Consultorio 301",
                "Confirmada"
        ));

        listaProxCitas.add(new CitasData(
                "Dra. María García",
                "Pediatría",
                "20 Nov 2024 - 3:00 PM",
                "Consultorio 205",
                "Pendiente"
        ));

        listaProxCitas.add(new CitasData(
                "Dr. Carlos López",
                "Dermatología",
                "25 Nov 2024 - 11:30 AM",
                "Consultorio 402",
                "Cancelada"
        ));

        adapter.notifyDataSetChanged();
    }
}