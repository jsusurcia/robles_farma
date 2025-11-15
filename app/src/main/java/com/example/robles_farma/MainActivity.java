package com.example.robles_farma;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.robles_farma.retrofit.FCMClient;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.ui.auth.AuthActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.robles_farma.databinding.ActivityMainBinding;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LoginStorage loginStorage; // 1. Declara una instancia de LoginStorage
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2. Inicializa LoginStorage PRIMERO
        loginStorage = new LoginStorage(this);

        boolean justLoggedIn = getIntent().getBooleanExtra("JUST_LOGGED_IN", false);

        // 4. Verificamos si el usuario pidió ser recordado en un inicio anterior.
        boolean rememberMe = loginStorage.isRememberMeEnabled();

        if (!rememberMe && !justLoggedIn) {

            Log.w("TOKEN_MAIN", "No hay sesión 'Recuérdame' activa y no es un inicio de sesión nuevo. Redirigiendo a AuthActivity.");

            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // Importante: no continuar con el onCreate
        }

        // 6. Verificación de validez del Token:
        // Si pasaste el filtro (porque tienes "Recuérdame" O "Acabas de Iniciar Sesión"),
        // AÚN DEBEMOS validar que el token no esté expirado.
        // Tu metodo isUserLoggedIn() ya hace esto perfectamente.
        if (!loginStorage.isUserLoggedIn()) {
            // isUserLoggedIn() ya valida el token y lo limpia si está expirado
            Log.w("TOKEN_MAIN", "Token expirado. Redirigiendo a AuthActivity.");

            Intent intent = new Intent(this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        // --- Si llegamos aquí, el usuario SÍ está logueado ---
        Log.d("TOKEN_MAIN", "Sesión válida encontrada. Cargando MainActivity.");

        if (!justLoggedIn) {
            FCMClient.registrarDispositivoFCM(this);
        }

        askForNotificationPermission();

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

    /**
     * Pide permiso para notificaciones en Android 13+
     */
    private void askForNotificationPermission() {
        // Solo aplica para Android 13 (API 33) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Verifica si el permiso AÚN NO ha sido concedido
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_DENIED) {

                // Muestra el diálogo de solicitud de permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
            // Si el permiso ya fue concedido, no hace nada.
        }
    }

    /**
     * Maneja la respuesta del usuario al diálogo de permiso
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Log.d("MainActivity", "Permiso POST_NOTIFICATIONS concedido.");
            } else {
                // Permiso denegado
                Log.w("MainActivity", "Permiso POST_NOTIFICATIONS denegado.");
                // Opcional: Mostrar un mensaje al usuario explicando por qué
                // se necesitan las notificaciones.
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
