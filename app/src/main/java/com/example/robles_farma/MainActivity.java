package com.example.robles_farma;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.robles_farma.retrofit.FCMClient;
import com.example.robles_farma.ui.auth.AuthActivity;
import com.example.robles_farma.ui.dialogs.CalificacionDialog;
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
    private LoginStorage loginStorage;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginStorage = new LoginStorage(this);

        boolean justLoggedIn = getIntent().getBooleanExtra("JUST_LOGGED_IN", false);
        boolean rememberMe = loginStorage.isRememberMeEnabled();

        if (!rememberMe && !justLoggedIn) {
            Log.w("TOKEN_MAIN", "No hay sesión 'Recuérdame' activa y no es un inicio de sesión nuevo.");
            irAlLogin();
            return;
        }

        if (!loginStorage.isUserLoggedIn()) {
            Log.w("TOKEN_MAIN", "Token expirado. Redirigiendo a AuthActivity.");
            irAlLogin();
            return;
        }

        Log.d("TOKEN_MAIN", "Sesión válida encontrada. Cargando MainActivity.");

        if (!justLoggedIn) {
            FCMClient.registrarDispositivoFCM(this);
        }

        askForNotificationPermission();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

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
            navController.popBackStack(navController.getGraph().getStartDestinationId(), false);
            navController.navigate(itemId);
            return true;
        });

        // 2. VERIFICAR SI LLEGAMOS POR UNA NOTIFICACIÓN (App estaba cerrada)
        checkIntentForNotification(getIntent());
    }

    // Método auxiliar para redirigir
    private void irAlLogin() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // 3. VERIFICAR SI LLEGAMOS POR UNA NOTIFICACIÓN (App estaba abierta o en segundo plano)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.d("NOTIFICACION", "onNewIntent disparado");
        checkIntentForNotification(intent);
    }

    /**
     * Revisa si el Intent trae datos de calificación ("action" y "cita_id")
     */
    private void checkIntentForNotification(Intent intent) {
        if (intent == null) return;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            // Extraemos los datos que mandamos desde Python (data payload)
            String action = extras.getString("action");
            String citaId = extras.getString("cita_id");

            Log.d("NOTIFICACION", "Action: " + action + " | CitaID: " + citaId);

            // Validamos si es la acción correcta
            if ("CALIFICAR_CITA".equals(action) && citaId != null) {
                mostrarDialogoCalificacion(citaId);
            }
        }
    }

    /**
     * Muestra el Diálogo (Interoperabilidad Java -> Kotlin)
     */
    private void mostrarDialogoCalificacion(String citaId) {
        // Como CalificacionDialog es Kotlin y 'newInstance' está en un companion object,
        // en Java se accede a través de '.Companion'.
        CalificacionDialog dialog = CalificacionDialog.Companion.newInstance(citaId);

        // Mostramos el diálogo usando getSupportFragmentManager()
        dialog.show(getSupportFragmentManager(), CalificacionDialog.TAG);
    }


    private void askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Permiso POST_NOTIFICATIONS concedido.");
            } else {
                Log.w("MainActivity", "Permiso POST_NOTIFICATIONS denegado.");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}