package com.example.robles_farma.ui.auth;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.robles_farma.R;
import com.example.robles_farma.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.btnIniciarSesion.setOnClickListener(v -> {
            iniciar_sesion();
        });

        return binding.getRoot();
    }

    private void iniciar_sesion() {
        String dni = binding.etDniLogin.getText().toString().trim();
        boolean recordarme = binding.chkRecordarme.isChecked();

        //Validar vacios
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

        //Lógica sencilla, luego se reemplaza con la llamada a la API
        if (dni.equals("72680893")) {
            Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
        }
    }
}