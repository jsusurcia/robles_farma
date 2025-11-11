package com.example.robles_farma;

import android.os.Bundle;
import android.util.Log;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.robles_farma.databinding.ActivityMainBinding;
import com.example.robles_farma.sharedpreferences.LoginStorage;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔹 Verificar si hay token guardado - iniciando comprobación
        String token = LoginStorage.getToken(this);
        if (token != null && !token.isEmpty()) {
            Log.d("TOKEN_MAIN", "Token cargado correctamente desde SharedPreferences");
        } else {
            Log.w("TOKEN_MAIN", "No se encontró token, usuario debe iniciar sesión");
        }

        // 🔹 Toolbar
        setSupportActionBar(binding.toolbar);

        // 🔹 Navegación inferior
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_citas,
                R.id.navigation_chat,
                R.id.navigation_perfil
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Limpia el back stack antes de cambiar de pestaña
            navController.popBackStack(navController.getGraph().getStartDestinationId(), false);

            // Navega al destino seleccionado
            navController.navigate(itemId);
            return true;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
