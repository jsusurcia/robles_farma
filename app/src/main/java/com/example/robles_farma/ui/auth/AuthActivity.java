package com.example.robles_farma.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.robles_farma.MainActivity;
import com.example.robles_farma.R;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.example.robles_farma.adapter.AuthPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private AuthPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔹 Verificamos si hay sesión activa antes de mostrar la UI
        LoginStorage loginStorage = new LoginStorage(this);
        if (loginStorage.isUserLoggedIn()) {
            String token = loginStorage.getToken();
            if (token != null && !token.isEmpty()) {
                RetrofitClient.API_TOKEN = token;
                startActivity(new Intent(this, MainActivity.class));
                finish(); // 👈 importante, así no vuelve al login
                return;
            }
        }

        // 🔹 Si no hay sesión, seguimos con el flujo normal
        setContentView(R.layout.activity_auth);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);

        // Configurar el adapter
        pagerAdapter = new AuthPagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);

        // Conectar TabLayout con ViewPager2
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
