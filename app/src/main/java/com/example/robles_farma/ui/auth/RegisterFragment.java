package com.example.robles_farma.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.robles_farma.MainActivity;
import com.example.robles_farma.R;
import com.example.robles_farma.request.RegisterRequest;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private AutoCompleteTextView autoTipoDocumento, autoSexo;
    private TextInputEditText etDNI, etNombres, etApellidoPaterno, etApellidoMaterno, etFechaNacimiento, etClave;
    private Button btnRegistrar;
    private CheckBox chkTerminos, chkSeguroSalud;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //Inicializar el TextInputLayout
        etDNI = view.findViewById(R.id.etDni);
        etNombres = view.findViewById(R.id.etNombres);
        etApellidoPaterno = view.findViewById(R.id.etApellidoPaterno);
        etApellidoMaterno = view.findViewById(R.id.etApellidoMaterno);
        autoTipoDocumento = view.findViewById(R.id.autoTipoDocumento);
        autoSexo = view.findViewById(R.id.autoSexo);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        etClave = view.findViewById(R.id.etClave);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        chkTerminos = view.findViewById(R.id.chkTerminos);
        chkSeguroSalud = view.findViewById(R.id.chkSeguroSalud);

        //Inicializar el servicio

        apiService = RetrofitClient.createService();

        // Configurar adaptadores para los AutoCompleteTextView
        String[] tiposDocumento = new String[]{"DNI", "Carnet de Extranjería", "Pasaporte"};
        ArrayAdapter<String> adapterTipoDoc = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, tiposDocumento);
        autoTipoDocumento.setAdapter(adapterTipoDoc);

        String[] sexos = new String[]{"Masculino", "Femenino"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, sexos);
        autoSexo.setAdapter(adapterSexo);


        etFechaNacimiento.setFocusable(false);


        //Configurar el click para abrir el calendario
        etFechaNacimiento.setOnClickListener(v ->
        {
            mostrarCalendario();
        });

        btnRegistrar.setOnClickListener(v -> {
            registrarPaciente();
        });

        return view;
    }

    private void mostrarCalendario() {

        final Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(), (DatePicker view, int year, int month, int dayOfMonth) -> {
            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(year, month, dayOfMonth);

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            etFechaNacimiento.setText(formato.format(fechaSeleccionada.getTime()));

        },
                anio,
                mes,
                dia
        );

        //Evita que selecciona fechas futuras
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        //Mostrar el calendario
        datePickerDialog.show();


    }

    private void registrarPaciente() {
        String nroDocumento = etDNI.getText().toString().trim();
        String nombres = etNombres.getText().toString().trim();
        String apellidoPaterno = etApellidoPaterno.getText().toString().trim();
        String apellidoMaterno = etApellidoMaterno.getText().toString().trim();
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String clave = etClave.getText().toString().trim();
        String tipoDocumento = autoTipoDocumento.getText().toString();
        String sexoStr = autoSexo.getText().toString();
        boolean esAsegurado = chkSeguroSalud.isChecked();




        if (TextUtils.isEmpty(nroDocumento) || TextUtils.isEmpty(nombres) || TextUtils.isEmpty(apellidoPaterno) || TextUtils.isEmpty(apellidoMaterno) || TextUtils.isEmpty(fechaNacimiento) || TextUtils.isEmpty(clave) || TextUtils.isEmpty(tipoDocumento) || TextUtils.isEmpty(sexoStr)) {
            Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!chkTerminos.isChecked()) {
            Toast.makeText(getContext(), "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        int idTipoDocumento = 1; // Ajustar según la selección del usuario
        boolean sexo = sexoStr.equals("Masculino");


        RegisterRequest registerRequest = new RegisterRequest(nroDocumento, clave, nombres, apellidoPaterno, apellidoMaterno, fechaNacimiento, sexo, idTipoDocumento, null, null, null, esAsegurado);

        apiService.registerPaciente(registerRequest).enqueue(new Callback<ItemResponse<PacienteResponse>>() {
            @Override
            public void onResponse(Call<ItemResponse<PacienteResponse>> call, Response<ItemResponse<PacienteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                    //Luego de registrar envia al main

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();


                } else {
                    Toast.makeText(getContext(), "Error en el registro. Inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ItemResponse<PacienteResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}