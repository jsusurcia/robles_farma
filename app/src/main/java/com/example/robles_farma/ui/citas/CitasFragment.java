package com.example.robles_farma.ui.citas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.robles_farma.R;
import com.example.robles_farma.adapter.CitasPagerAdapter;
import com.example.robles_farma.databinding.FragmentCitasBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class CitasFragment extends Fragment {
    private FragmentCitasBinding binding;
    private CitasPagerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCitasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CitasPagerAdapter(getChildFragmentManager(), getLifecycle());
        binding.viewPagerCitas.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayoutCitas, binding.viewPagerCitas, (tab, position) -> {
            if (position == 0) {
                tab.setText("Próximas");
            } else {
                tab.setText("Pasadas");
            }
        }).attach();
    }
}