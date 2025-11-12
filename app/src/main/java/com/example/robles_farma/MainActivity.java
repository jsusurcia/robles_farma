package com.example.robles_farma;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.robles_farma.ui.auth.AuthActivity;
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
    private LoginStorage loginStorage; // 1. Declara una instancia de LoginStorage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Inicializa LoginStorage PRIMERO
        loginStorage = new LoginStorage(this);

        if (!loginStorage.isUserLoggedIn()) {

            Log.w("TOKEN_MAIN", "No hay sesión válida o token expirado. Redirigiendo a LoginActivity.");

            Intent intent = new Intent(this, AuthActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
            return;
        }

        // --- Si llegamos aquí, el usuario SÍ está logueado ---
        Log.d("TOKEN_MAIN", "Sesión válida encontrada. Cargando MainActivity.");

        // 7. Ahora sí, inflamos la vista y configuramos el resto
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
