package com.example.robles_farma.ui.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.robles_farma.R;
import com.example.robles_farma.adapter.AuthPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private AuthPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);

        //Configurar el adapter
        pagerAdapter = new AuthPagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        //Conectar TabLayout con el ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2, (tab, i) -> {
            switch (i) {
                case 0:
                    tab.setText("Inicia sesión");
                    break;
                case 1:
                    tab.setText("Regístrate");
                    break;
            }
        }).attach();
    }

    public void switchToTab(int position) {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(position, true);
        }
    }
}