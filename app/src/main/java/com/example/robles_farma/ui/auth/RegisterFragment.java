package com.example.robles_farma.ui.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.robles_farma.R;
import com.example.robles_farma.request.RegisterRequest;
import com.example.robles_farma.response.ItemListResponse;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private AutoCompleteTextView autoTipoDocumento, autoSexo;
    private TextInputEditText etDNI, etNombres, etApellidoPaterno, etApellidoMaterno;
    private TextInputEditText etFechaNacimiento, etClave, etConfirmarClave;
    private TextInputEditText etContactoEmergenciaNombre, etNumeroEmergencia;
    private Button btnRegistrar;
    private CheckBox chkTerminos, chkSeguroSalud;
    private ApiService apiService;

    // TextInputLayouts para mostrar errores
    private TextInputLayout tilDNI, tilClave, tilConfirmarClave;

    // Variable para almacenar documentos registrados
    private List<String> documentosRegistrados = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Inicializar vistas
        inicializarVistas(view);

        // Inicializar el servicio
        apiService = RetrofitClient.createService();

        // Cargar documentos registrados
        cargarDocumentosRegistrados();

        // Configurar adaptadores
        configurarAdaptadores();

        // Configurar fecha de nacimiento
        configurarFechaNacimiento();

        // Configurar validaciones en tiempo real
        configurarValidaciones();

        // Configurar botón de registro
        btnRegistrar.setOnClickListener(v -> registrarPaciente());

        return view;
    }

    private void inicializarVistas(View view) {
        etDNI = view.findViewById(R.id.etDni);
        etNombres = view.findViewById(R.id.etNombres);
        etApellidoPaterno = view.findViewById(R.id.etApellidoPaterno);
        etApellidoMaterno = view.findViewById(R.id.etApellidoMaterno);
        autoTipoDocumento = view.findViewById(R.id.autoTipoDocumento);
        autoSexo = view.findViewById(R.id.autoSexo);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        etClave = view.findViewById(R.id.etClave);
        etConfirmarClave = view.findViewById(R.id.etConfirmarClave);
        etContactoEmergenciaNombre = view.findViewById(R.id.etContactoEmergenciaNombre);
        etNumeroEmergencia = view.findViewById(R.id.etNumeroEmergencia);
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        chkTerminos = view.findViewById(R.id.chkTerminos);
        chkSeguroSalud = view.findViewById(R.id.chkSeguroSalud);

        // Obtener los TextInputLayouts
        tilDNI = (TextInputLayout) etDNI.getParent().getParent();
        tilClave = (TextInputLayout) etClave.getParent().getParent();
        tilConfirmarClave = (TextInputLayout) etConfirmarClave.getParent().getParent();
    }

    private void configurarAdaptadores() {
        // Tipo de documento
        String[] tiposDocumento = new String[]{"DNI", "Carnet de Extranjería", "Pasaporte"};
        ArrayAdapter<String> adapterTipoDoc = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, tiposDocumento);
        autoTipoDocumento.setAdapter(adapterTipoDoc);

        // Sexo
        String[] sexos = new String[]{"Masculino", "Femenino"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, sexos);
        autoSexo.setAdapter(adapterSexo);
    }

    private void configurarFechaNacimiento() {
        etFechaNacimiento.setFocusable(false);
        etFechaNacimiento.setOnClickListener(v -> mostrarCalendario());
    }

    // MÉTODO: Cargar documentos registrados desde el backend
    // En cargarDocumentosRegistrados(), agregar try-catch
    private void cargarDocumentosRegistrados() {
        apiService.getPacientes().enqueue(new Callback<ItemListResponse<PacienteResponse>>() {
            @Override
            public void onResponse(Call<ItemListResponse<PacienteResponse>> call,
                                   Response<ItemListResponse<PacienteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    documentosRegistrados.clear();

                    List<PacienteResponse> pacientes = response.body().getData();
                    if (pacientes != null) {
                        for (PacienteResponse paciente : pacientes) {
                            if (paciente.getNroDocumento() != null) {
                                documentosRegistrados.add(paciente.getNroDocumento());
                            }
                        }
                        Log.d("REGISTER", "Documentos cargados: " + documentosRegistrados.size());
                    }
                } else {
                    // AGREGAR ESTO PARA VER EL ERROR
                    Log.e("REGISTER", "Error code: " + response.code());
                    Log.e("REGISTER", "Error body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ItemListResponse<PacienteResponse>> call, Throwable t) {
                Log.e("REGISTER", "Error al cargar documentos: " + t.getMessage());
                t.printStackTrace(); // AGREGAR ESTO
            }
        });
    }

    // CONFIGURAR TODAS LAS VALIDACIONES EN TIEMPO REAL
    private void configurarValidaciones() {
        // Validar tipo de documento según selección
        autoTipoDocumento.setOnItemClickListener((parent, view, position, id) -> {
            etDNI.setText("");
            tilDNI.setError(null);
            tilDNI.setErrorEnabled(false);
        });

        // Validar documento mientras se escribe
        etDNI.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarDocumento(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validar fortaleza de contraseña
        etClave.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarFortalezaContrasena(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validar coincidencia de contraseñas
        etConfirmarClave.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarCoincidenciaContrasenas();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // VALIDAR DOCUMENTO SEGÚN TIPO (con validación de duplicados)
    private void validarDocumento(String documento) {
        String tipoDoc = autoTipoDocumento.getText().toString();

        if (tipoDoc.isEmpty()) {
            tilDNI.setError("Primero seleccione el tipo de documento");
            tilDNI.setErrorEnabled(true);
            return;
        }

        boolean esValido = false;
        String mensajeError = "";

        switch (tipoDoc) {
            case "DNI":
                esValido = documento.matches("^\\d{8}$");
                if (!esValido && !documento.isEmpty()) {
                    if (documento.length() < 8) {
                        mensajeError = "DNI debe tener 8 dígitos (" + documento.length() + "/8)";
                    } else if (documento.length() > 8) {
                        mensajeError = "DNI debe tener exactamente 8 dígitos";
                    } else {
                        mensajeError = "DNI solo debe contener números";
                    }
                }
                break;

            case "Carnet de Extranjería":
                esValido = documento.matches("^\\d{9}$");
                if (!esValido && !documento.isEmpty()) {
                    if (documento.length() < 9) {
                        mensajeError = "Carnet debe tener 9 dígitos (" + documento.length() + "/9)";
                    } else if (documento.length() > 9) {
                        mensajeError = "Carnet debe tener exactamente 9 dígitos";
                    } else {
                        mensajeError = "Carnet solo debe contener números";
                    }
                }
                break;

            case "Pasaporte":
                esValido = documento.matches("^[A-Z0-9]{9,12}$");
                if (!esValido && !documento.isEmpty()) {
                    if (documento.length() < 9) {
                        mensajeError = "Pasaporte debe tener entre 9-12 caracteres (" + documento.length() + "/9)";
                    } else if (documento.length() > 12) {
                        mensajeError = "Pasaporte debe tener máximo 12 caracteres";
                    } else {
                        mensajeError = "Pasaporte: solo letras mayúsculas y números";
                    }
                }
                break;
        }

        // VALIDACIÓN DE DUPLICADOS
        if (esValido && documentoYaRegistrado(documento)) {
            mensajeError = "Este documento ya está registrado";
            esValido = false;
        }

        // Mostrar error si no es válido
        if (!esValido && !documento.isEmpty()) {
            tilDNI.setError(mensajeError);
            tilDNI.setErrorEnabled(true);
        } else {
            tilDNI.setError(null);
            tilDNI.setErrorEnabled(false);
        }
    }

    // VALIDAR FORTALEZA DE CONTRASEÑA
    private void validarFortalezaContrasena(String password) {
        List<String> errores = new ArrayList<>();

        if (password.length() >= 5) {
            errores.add("Mínimo 5 caracteres");

        } else {
            tilClave.setError(null);
            tilClave.setErrorEnabled(false);
        }
    }

    // VALIDAR COINCIDENCIA DE CONTRASEÑAS
    private void validarCoincidenciaContrasenas() {
        String clave = etClave.getText().toString();
        String confirmarClave = etConfirmarClave.getText().toString();

        if (!confirmarClave.isEmpty() && !clave.equals(confirmarClave)) {
            tilConfirmarClave.setError("Las contraseñas no coinciden");
            tilConfirmarClave.setErrorEnabled(true);
        } else {
            tilConfirmarClave.setError(null);
            tilConfirmarClave.setErrorEnabled(false);
        }
    }

    // VALIDAR SI LA CONTRASEÑA ES SEGURA
    private boolean esContrasenaSegura(String password) {
        return password.length() >= 5;
    }

    // VALIDAR DOCUMENTO SEGÚN TIPO (método auxiliar)
    private boolean esDocumentoValido(String documento, String tipoDoc) {
        switch (tipoDoc) {
            case "DNI":
                return documento.matches("^\\d{8}$");
            case "Carnet de Extranjería":
                return documento.matches("^\\d{9}$");
            case "Pasaporte":
                return documento.matches("^[A-Z0-9]{9,12}$");
            default:
                return false;
        }
    }

    // MÉTODO AUXILIAR: Verificar si documento ya está registrado
    private boolean documentoYaRegistrado(String documento) {
        return documentosRegistrados.contains(documento);
    }

    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        calendario.add(Calendar.YEAR, -18);

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    Calendar fechaSeleccionada = Calendar.getInstance();
                    fechaSeleccionada.set(year, month, dayOfMonth);

                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etFechaNacimiento.setText(formato.format(fechaSeleccionada.getTime()));
                },
                anio, mes, dia
        );

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -120);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void registrarPaciente() {
        String nroDocumento = etDNI.getText().toString().trim();
        String nombres = etNombres.getText().toString().trim();
        String apellidoPaterno = etApellidoPaterno.getText().toString().trim();
        String apellidoMaterno = etApellidoMaterno.getText().toString().trim();
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String clave = etClave.getText().toString().trim();
        String confirmarClave = etConfirmarClave.getText().toString().trim();
        String tipoDocumento = autoTipoDocumento.getText().toString();
        String sexoStr = autoSexo.getText().toString();
        String contactoEmergenciaNombre = etContactoEmergenciaNombre.getText().toString().trim();
        String numeroEmergencia = etNumeroEmergencia.getText().toString().trim();
        boolean esAsegurado = chkSeguroSalud.isChecked();

        // VALIDACIÓN 1: Campos vacíos
        if (TextUtils.isEmpty(tipoDocumento)) {
            Toast.makeText(getContext(), "Seleccione el tipo de documento", Toast.LENGTH_SHORT).show();
            autoTipoDocumento.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nroDocumento)) {
            Toast.makeText(getContext(), "Ingrese su número de documento", Toast.LENGTH_SHORT).show();
            etDNI.requestFocus();
            return;
        }

        // VALIDACIÓN 2: Documento según tipo
        if (!esDocumentoValido(nroDocumento, tipoDocumento)) {
            String mensaje = "";
            switch (tipoDocumento) {
                case "DNI":
                    mensaje = "DNI debe tener exactamente 8 dígitos";
                    break;
                case "Carnet de Extranjería":
                    mensaje = "Carnet de Extranjería debe tener 9 dígitos";
                    break;
                case "Pasaporte":
                    mensaje = "Pasaporte debe tener entre 9-12 caracteres alfanuméricos en mayúscula";
                    break;
            }
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
            etDNI.requestFocus();
            return;
        }

        //  Verificar si el documento ya está registrado
        if (documentoYaRegistrado(nroDocumento)) {
            Toast.makeText(getContext(),
                    "Este documento ya está registrado en el sistema",
                    Toast.LENGTH_LONG).show();
            etDNI.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nombres)) {
            Toast.makeText(getContext(), "Ingrese sus nombres", Toast.LENGTH_SHORT).show();
            etNombres.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(apellidoPaterno)) {
            Toast.makeText(getContext(), "Ingrese su apellido paterno", Toast.LENGTH_SHORT).show();
            etApellidoPaterno.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(apellidoMaterno)) {
            Toast.makeText(getContext(), "Ingrese su apellido materno", Toast.LENGTH_SHORT).show();
            etApellidoMaterno.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(fechaNacimiento)) {
            Toast.makeText(getContext(), "Seleccione su fecha de nacimiento", Toast.LENGTH_SHORT).show();
            etFechaNacimiento.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(sexoStr)) {
            Toast.makeText(getContext(), "Seleccione su sexo", Toast.LENGTH_SHORT).show();
            autoSexo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(clave)) {
            Toast.makeText(getContext(), "Ingrese una contraseña", Toast.LENGTH_SHORT).show();
            etClave.requestFocus();
            return;
        }

        if (!esContrasenaSegura(clave)) {
            Toast.makeText(getContext(),
                    "La contraseña debe tener al menos 5 caracteres",
                    Toast.LENGTH_LONG).show();
            etClave.requestFocus();
            return;

        }

        if (TextUtils.isEmpty(confirmarClave)) {
            Toast.makeText(getContext(), "Confirme su contraseña", Toast.LENGTH_SHORT).show();
            etConfirmarClave.requestFocus();
            return;
        }

        if (!clave.equals(confirmarClave)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            etConfirmarClave.requestFocus();
            return;
        }

        if (!chkTerminos.isChecked()) {
            Toast.makeText(getContext(), "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        // Preparar datos para el registro
        int idTipoDocumento = obtenerIdTipoDocumento(tipoDocumento);
        boolean sexo = sexoStr.equals("Masculino");

        RegisterRequest registerRequest = new RegisterRequest(
                nroDocumento,
                clave,
                nombres,
                apellidoPaterno,
                apellidoMaterno,
                fechaNacimiento,
                sexo,
                idTipoDocumento,
                null,
                contactoEmergenciaNombre.isEmpty() ? null : contactoEmergenciaNombre,
                numeroEmergencia.isEmpty() ? null : numeroEmergencia,
                esAsegurado
        );

        // Llamar a la API
        apiService.registerPaciente(registerRequest).enqueue(new Callback<ItemResponse<PacienteResponse>>() {
            @Override
            public void onResponse(Call<ItemResponse<PacienteResponse>> call, Response<ItemResponse<PacienteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(),
                            "" + response.body().getMessage(),
                            Toast.LENGTH_LONG).show();

                    // Cambiar a la pestaña de login
                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).switchToTab(0);
                    }

                } else {
                    // MANEJAR ERRORES ESPECÍFICOS DEL BACKEND
                    String mensajeError = "Error en el registro";

                    try {
                        if (response.code() == 400 || response.code() == 409) {
                            // Error 400/409 = datos duplicados o inválidos
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e("REGISTER_ERROR", "Error del backend: " + errorBody);

                                // Verificar si el error es por documento duplicado
                                if (errorBody.toLowerCase().contains("documento") ||
                                        errorBody.toLowerCase().contains("duplicado") ||
                                        errorBody.toLowerCase().contains("ya existe") ||
                                        errorBody.toLowerCase().contains("already exists") ||
                                        errorBody.toLowerCase().contains("unique constraint")) {

                                    mensajeError = "Este documento ya está registrado";

                                    // Agregar a la lista para futuras validaciones
                                    String docActual = etDNI.getText().toString().trim();
                                    if (!documentosRegistrados.contains(docActual)) {
                                        documentosRegistrados.add(docActual);
                                        Log.d("REGISTER", "Documento agregado a lista: " + docActual);
                                    }

                                    // Marcar error en el campo
                                    tilDNI.setError("Este documento ya está registrado");
                                    etDNI.requestFocus();
                                } else {
                                    // Mostrar el mensaje del backend si es otro tipo de error
                                    mensajeError = errorBody.length() < 200 ? errorBody : "Datos inválidos. Verifique la información";
                                }
                            }
                        } else if (response.code() == 500) {
                            mensajeError = "Error en el servidor. Intente más tarde";
                        } else {
                            mensajeError = "Error en el registro (Código: " + response.code() + ")";
                        }
                    } catch (Exception e) {
                        Log.e("REGISTER_ERROR", "Error leyendo respuesta: " + e.getMessage());
                        e.printStackTrace();
                    }

                    Toast.makeText(getContext(), mensajeError, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ItemResponse<PacienteResponse>> call, Throwable t) {
                Log.e("REGISTER_ERROR", "Error de conexión: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // MÉTODO AUXILIAR: Obtener ID del tipo de documento
    private int obtenerIdTipoDocumento(String tipoDoc) {
        switch (tipoDoc) {
            case "DNI":
                return 1;
            case "Carnet de Extranjería":
                return 2;
            case "Pasaporte":
                return 3;
            default:
                return 1;
        }
    }
}