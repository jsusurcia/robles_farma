package com.example.robles_farma.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.robles_farma.ui.auth.LoginFragment;
import com.example.robles_farma.ui.auth.RegisterFragment;

public class AuthPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 2;

    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LoginFragment();
            case 1:
                return new RegisterFragment();
            default:
                return new LoginFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
