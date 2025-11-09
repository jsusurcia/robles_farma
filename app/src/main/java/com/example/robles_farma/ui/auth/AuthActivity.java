package com.example.robles_farma.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        // 🔹 Verificar si hay sesión activa Y si el token es válido (no expirado)
        LoginStorage loginStorage = new LoginStorage(this);

        if (loginStorage.isUserLoggedIn()) {
            //  Este método ahora valida automáticamente si el token NO está expirado
            String token = LoginStorage.getToken(this);

            if (token != null && !token.isEmpty()) {
                RetrofitClient.API_TOKEN = token;
                Log.i("AuthActivity", " Token válido encontrado, redirigiendo a MainActivity");

                startActivity(new Intent(this, MainActivity.class));
                finish(); // Importante: cerrar AuthActivity para que no vuelva al login
                return;
            }
        } else {
            Log.w("AuthActivity", " No hay sesión válida o el token expiró");
        }

        // 🔹 Si no hay sesión válida o el token expiró, mostrar el login
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