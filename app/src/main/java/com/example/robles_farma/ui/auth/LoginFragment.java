package com.example.robles_farma.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.robles_farma.MainActivity;
import com.example.robles_farma.databinding.FragmentLoginBinding;
import com.example.robles_farma.request.LoginRequest;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.LoginResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.example.robles_farma.sharedpreferences.LoginStorage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private ApiService apiService;
    private LoginStorage loginStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.createService();
        loginStorage = new LoginStorage(requireContext());

        binding.btnIniciarSesion.setOnClickListener(v -> {
            iniciar_sesion();
        });

        // Cargar credenciales si existen
        if (loginStorage.isRememberMeEnabled()) {
            binding.etDniLogin.setText(loginStorage.getDni());
            binding.etClaveLogin.setText(loginStorage.getClave());
            binding.chkRecordarme.setChecked(true);
        }

        return binding.getRoot();
    }

    private void iniciar_sesion() {
        String dni = binding.etDniLogin.getText().toString().trim();
        String clave = binding.etClaveLogin.getText().toString().trim();
        boolean recordarme = binding.chkRecordarme.isChecked();

        if (dni.isEmpty()) {
            binding.etDniLogin.setError("Por favor, ingrese su DNI");
            binding.etDniLogin.requestFocus();
            return;
        }
        if (dni.length() != 8) {
            binding.etDniLogin.setError("El DNI debe de tener 8 dígitos");
            binding.etDniLogin.requestFocus();
            return;
        }
        if (clave.isEmpty()) {
            binding.etClaveLogin.setError("Por favor, ingrese su contraseña");
            binding.etClaveLogin.requestFocus();
            return;
        }

        binding.btnIniciarSesion.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        LoginRequest loginRequest = new LoginRequest(dni, clave);
        apiService.login(loginRequest).enqueue(new Callback<ItemResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ItemResponse<LoginResponse>> call, Response<ItemResponse<LoginResponse>> response) {
                binding.btnIniciarSesion.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getStatus().equals("success")) {
                    LoginResponse loginResponse = response.body().getData();

                    RetrofitClient.API_TOKEN = loginResponse.getAccessToken();
                    Log.e("TOKEN_GUARDADO", loginResponse.getAccessToken());


                    // --- INICIO DE CAMBIOS ---
                    if (recordarme) {
                        // Esto está bien: guarda todo, incluyendo 'rememberMe = true'
                        loginStorage.saveLoginCredentials(dni, clave, loginResponse.getAccessToken(), loginResponse.getPaciente());
                    } else {
                        // CAMBIO 1: No borres las credenciales.
                        // Usa 'saveSession' para guardar el token actual pero con 'rememberMe = false'.
                        loginStorage.saveSession(loginResponse.getAccessToken(), loginResponse.getPaciente());
                    }

                    Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MainActivity.class);

                    // CAMBIO 2: Añadimos un "flag" para que MainActivity sepa
                    // que venimos de un login exitoso.
                    intent.putExtra("JUST_LOGGED_IN", true);

                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String errorMessage = "Error en el inicio de sesión";
                    if (response.body() != null && response.body().getMessage() != null){
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ItemResponse<LoginResponse>> call, Throwable t) {
                binding.btnIniciarSesion.setEnabled(true);
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}