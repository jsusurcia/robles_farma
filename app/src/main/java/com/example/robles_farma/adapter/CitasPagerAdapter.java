package com.example.robles_farma.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.robles_farma.ui.citas.PasadasCitasFragment;
import com.example.robles_farma.ui.citas.ProximasCitasFragment;

public class CitasPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;

    public CitasPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ProximasCitasFragment();
            case 1:
                return new PasadasCitasFragment();
            default:
                return new ProximasCitasFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
