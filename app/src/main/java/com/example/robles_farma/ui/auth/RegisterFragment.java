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
import com.example.robles_farma.response.TipoDocumentoResponse;
import com.example.robles_farma.retrofit.ApiService;
import com.example.robles_farma.retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    private AutoCompleteTextView autoTipoDocumento, autoSexo;
    private TextInputEditText etDNI, etNombres, etApellidoPaterno, etApellidoMaterno;
    private TextInputEditText etFechaNacimiento, etClave, etConfirmarClave, etDomicilio;
    private TextInputEditText etContactoEmergenciaNombre, etNumeroEmergencia;
    private Button btnRegistrar;
    private CheckBox chkTerminos, chkSeguroSalud;
    private ApiService apiService;

    // TextInputLayouts para mostrar errores
    private TextInputLayout tilDNI, tilClave, tilConfirmarClave;

    // Variables para almacenar datos
    private List<String> documentosRegistrados = new ArrayList<>();
    private List<TipoDocumentoResponse> listaTiposDocumento = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Inicializar vistas
        inicializarVistas(view);

        // Inicializar el servicio
        apiService = RetrofitClient.createService();

        // Cargar datos desde el backend
        cargarTiposDocumento();
        cargarDocumentosRegistrados();

        // Configurar adaptadores fijos
        configurarAdaptadores();

        // Configurar fecha de nacimiento
        configurarFechaNacimiento();

        // Configurar validaciones en tiempo real
        configurarValidaciones();

        configurarTerminosClickeables();

        // Configurar botón de registro
        btnRegistrar.setOnClickListener(v -> registrarPaciente());

        return view;
    }

    private void inicializarVistas(View view) {
        etDNI = view.findViewById(R.id.etDni);
        etNombres = view.findViewById(R.id.etNombres);
        etApellidoPaterno = view.findViewById(R.id.etApellidoPaterno);
        etApellidoMaterno = view.findViewById(R.id.etApellidoMaterno);
        etDomicilio = view.findViewById(R.id.etDomicilio);
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

    // ========== CARGAR TIPOS DE DOCUMENTO DESDE BD ==========
    private void cargarTiposDocumento() {
        apiService.getTiposDocumento().enqueue(new Callback<ItemListResponse<TipoDocumentoResponse>>() {
            @Override
            public void onResponse(Call<ItemListResponse<TipoDocumentoResponse>> call,
                                   Response<ItemListResponse<TipoDocumentoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTiposDocumento = response.body().getData();

                    if (listaTiposDocumento != null && !listaTiposDocumento.isEmpty()) {
                        // Crear array de nombres para el AutoCompleteTextView
                        String[] nombresTiposDoc = new String[listaTiposDocumento.size()];
                        for (int i = 0; i < listaTiposDocumento.size(); i++) {
                            nombresTiposDoc[i] = listaTiposDocumento.get(i).getTipoDocumento();
                        }

                        // Configurar el adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                nombresTiposDoc
                        );
                        autoTipoDocumento.setAdapter(adapter);

                        Log.d("TIPO_DOC", "Tipos de documento cargados: " + listaTiposDocumento.size());
                    } else {
                        Log.w("TIPO_DOC", "No se encontraron tipos de documento");
                        Toast.makeText(getContext(),
                                "No se pudieron cargar los tipos de documento",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TIPO_DOC", "Error code: " + response.code());
                    Toast.makeText(getContext(),
                            "Error al cargar tipos de documento",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ItemListResponse<TipoDocumentoResponse>> call, Throwable t) {
                Log.e("TIPO_DOC", "Error de conexión: " + t.getMessage());
                t.printStackTrace();
                Toast.makeText(getContext(),
                        "Error de conexión al cargar tipos de documento",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========== FILTRO SOLO LETRAS ==========
    private void configurarFiltroSoloLetras(TextInputEditText editText) {
        android.text.InputFilter filtroLetras = new android.text.InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                // Bloquear solo números
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (Character.isDigit(c)) {
                        return "";
                    }
                }

                return null;
            }
        };

        android.text.InputFilter[] filtrosActuales = editText.getFilters();
        android.text.InputFilter[] nuevosFiltros = new android.text.InputFilter[filtrosActuales.length + 1];
        System.arraycopy(filtrosActuales, 0, nuevosFiltros, 0, filtrosActuales.length);
        nuevosFiltros[filtrosActuales.length] = filtroLetras;
        editText.setFilters(nuevosFiltros);
    }


    // ========== CONFIGURAR MAX LENGTH DINÁMICO ==========
    private void configurarMaxLengthDocumento(String nombreTipoDoc) {
        String tipoNormalizado = nombreTipoDoc.toLowerCase();
        int maxLength = 12; // Valor por defecto

        // Determinar maxLength según el tipo
        if (tipoNormalizado.contains("dni")) {
            maxLength = 8;
        } else if (tipoNormalizado.contains("carnet") ||
                tipoNormalizado.contains("extranjería") ||
                tipoNormalizado.contains("extranjeria")) {
            maxLength = 9;
        } else if (tipoNormalizado.contains("pasaporte")) {
            maxLength = 12;
        }

        // Aplicar el filtro de longitud
        android.text.InputFilter[] filters = new android.text.InputFilter[1];
        filters[0] = new android.text.InputFilter.LengthFilter(maxLength);
        etDNI.setFilters(filters);

        Log.d("TIPO_DOC", "MaxLength configurado: " + maxLength + " para " + nombreTipoDoc);
    }

    // ========== OBTENER ID DINÁMICO DEL TIPO DE DOCUMENTO ==========
    private int obtenerIdTipoDocumento(String nombreTipoDoc) {
        for (TipoDocumentoResponse tipo : listaTiposDocumento) {
            if (tipo.getTipoDocumento().equals(nombreTipoDoc)) {
                return tipo.getIdTipoDocumento();
            }
        }
        return -1; // Retorna -1 si no se encuentra
    }

    //   OBTENER PATRÓN DE VALIDACIÓN DINÁMICO
    private String obtenerPatronValidacion(String nombreTipoDoc) {
        String tipoNormalizado = nombreTipoDoc.toLowerCase();

        // Detectar tipo de documento por palabras clave
        if (tipoNormalizado.contains("dni")) {
            return "^\\d{8}$";
        } else if (tipoNormalizado.contains("carnet") ||
                tipoNormalizado.contains("extranjería") ||
                tipoNormalizado.contains("extranjeria")) {
            return "^\\d{9}$";
        } else if (tipoNormalizado.contains("pasaporte")) {
            return "^[A-Z0-9]{9,12}$";
        }

        return "^.+$"; // Patrón por defecto
    }

    //  OBTENER MENSAJE DE ERROR DINÁMICO
    private String obtenerMensajeError(String nombreTipoDoc, String documento) {
        String tipoNormalizado = nombreTipoDoc.toLowerCase();

        if (tipoNormalizado.contains("dni")) {
            if (documento.length() < 8) {
                return "DNI debe tener 8 dígitos (" + documento.length() + "/8)";
            } else if (documento.length() > 8) {
                return "DNI debe tener exactamente 8 dígitos";
            } else {
                return "DNI solo debe contener números";
            }
        } else if (tipoNormalizado.contains("carnet") ||
                tipoNormalizado.contains("extranjería") ||
                tipoNormalizado.contains("extranjeria")) {
            if (documento.length() < 9) {
                return "Carnet debe tener 9 dígitos (" + documento.length() + "/9)";
            } else if (documento.length() > 9) {
                return "Carnet debe tener exactamente 9 dígitos";
            } else {
                return "Carnet solo debe contener números";
            }
        } else if (tipoNormalizado.contains("pasaporte")) {
            if (documento.length() < 9) {
                return "Pasaporte debe tener entre 9-12 caracteres (" + documento.length() + "/9)";
            } else if (documento.length() > 12) {
                return "Pasaporte debe tener máximo 12 caracteres";
            } else {
                return "Pasaporte: solo letras mayúsculas y números";
            }
        }

        return "Formato de documento no válido";
    }

    private void configurarAdaptadores() {
        // Sexo (este permanece fijo)
        String[] sexos = new String[]{"Masculino", "Femenino"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, sexos);
        autoSexo.setAdapter(adapterSexo);
    }

    private void configurarFechaNacimiento() {
        etFechaNacimiento.setFocusable(false);
        etFechaNacimiento.setOnClickListener(v -> mostrarCalendario());
    }

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
                    Log.e("REGISTER", "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ItemListResponse<PacienteResponse>> call, Throwable t) {
                Log.e("REGISTER", "Error al cargar documentos: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void configurarValidaciones() {
        // Validar tipo de documento según selección
        autoTipoDocumento.setOnItemClickListener((parent, view, position, id) -> {
            etDNI.setText("");
            tilDNI.setError(null);
            tilDNI.setErrorEnabled(false);

            // Configurar maxLength dinámicamente según el tipo de documento
            String tipoSeleccionado = autoTipoDocumento.getText().toString();
            configurarMaxLengthDocumento(tipoSeleccionado);
        });

        //Filtro para campos de texto (solo letras)
        configurarFiltroSoloLetras(etNombres);
        configurarFiltroSoloLetras(etApellidoPaterno);
        configurarFiltroSoloLetras(etApellidoMaterno);
        configurarFiltroSoloLetras(etContactoEmergenciaNombre);

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

    // ========== VALIDACIÓN DINÁMICA DE DOCUMENTO ==========
    private void validarDocumento(String documento) {
        String tipoDoc = autoTipoDocumento.getText().toString();

        if (tipoDoc.isEmpty()) {
            tilDNI.setError("Primero seleccione el tipo de documento");
            tilDNI.setErrorEnabled(true);
            return;
        }

        if (documento.isEmpty()) {
            tilDNI.setError(null);
            tilDNI.setErrorEnabled(false);
            return;
        }

        // Obtener patrón de validación dinámico
        String patron = obtenerPatronValidacion(tipoDoc);
        boolean esValido = documento.matches(patron);
        String mensajeError = "";

        // Mensaje de error personalizado
        if (!esValido) {
            mensajeError = obtenerMensajeError(tipoDoc, documento);
        }

        // VALIDACIÓN DE DUPLICADOS
        if (esValido && documentoYaRegistrado(documento)) {
            mensajeError = "Este documento ya está registrado";
            esValido = false;
        }

        // Mostrar error si no es válido
        if (!esValido) {
            tilDNI.setError(mensajeError);
            tilDNI.setErrorEnabled(true);
        } else {
            tilDNI.setError(null);
            tilDNI.setErrorEnabled(false);
        }
    }

    private void validarFortalezaContrasena(String password) {
        if (password.length() < 5 && !password.isEmpty()) {
            tilClave.setError("Mínimo 5 caracteres");
            tilClave.setErrorEnabled(true);
        } else {
            tilClave.setError(null);
            tilClave.setErrorEnabled(false);
        }
    }

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

    private boolean esContrasenaSegura(String password) {
        return password.length() >= 5;
    }

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
        String domicilio = etDomicilio.getText().toString().trim();
        String fechaNacimiento = etFechaNacimiento.getText().toString().trim();
        String clave = etClave.getText().toString().trim();
        String confirmarClave = etConfirmarClave.getText().toString().trim();
        String tipoDocumento = autoTipoDocumento.getText().toString();
        String sexoStr = autoSexo.getText().toString();
        String contactoEmergenciaNombre = etContactoEmergenciaNombre.getText().toString().trim();
        String numeroEmergencia = etNumeroEmergencia.getText().toString().trim();
        boolean esAsegurado = chkSeguroSalud.isChecked();

        // VALIDACIÓN 1: Tipo de documento
        if (TextUtils.isEmpty(tipoDocumento)) {
            Toast.makeText(getContext(), "Seleccione el tipo de documento", Toast.LENGTH_SHORT).show();
            autoTipoDocumento.requestFocus();
            return;
        }

        // OBTENER ID DINÁMICO DEL TIPO DE DOCUMENTO
        int idTipoDocumento = obtenerIdTipoDocumento(tipoDocumento);
        if (idTipoDocumento == -1) {
            Toast.makeText(getContext(), "Tipo de documento no válido", Toast.LENGTH_SHORT).show();
            autoTipoDocumento.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nroDocumento)) {
            Toast.makeText(getContext(), "Ingrese su número de documento", Toast.LENGTH_SHORT).show();
            etDNI.requestFocus();
            return;
        }

        // VALIDACIÓN DINÁMICA: Documento según patrón
        String patron = obtenerPatronValidacion(tipoDocumento);
        if (!nroDocumento.matches(patron)) {
            Toast.makeText(getContext(),
                    "El formato del documento no es válido para " + tipoDocumento,
                    Toast.LENGTH_LONG).show();
            etDNI.requestFocus();
            return;
        }

        // Verificar si el documento ya está registrado
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
                domicilio.isEmpty() ? null : domicilio,
                contactoEmergenciaNombre.isEmpty() ? null : contactoEmergenciaNombre,
                numeroEmergencia.isEmpty() ? null : numeroEmergencia,
                esAsegurado
        );

        // Log para debug
        Log.d("REGISTER", "ID Tipo Documento: " + idTipoDocumento + " (" + tipoDocumento + ")");

        // Llamar a la API
        apiService.registerPaciente(registerRequest).enqueue(new Callback<ItemResponse<PacienteResponse>>() {
            @Override
            public void onResponse(Call<ItemResponse<PacienteResponse>> call, Response<ItemResponse<PacienteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(),
                            response.body().getMessage(),
                            Toast.LENGTH_LONG).show();

                    limpiarFormulario();

                    // Cambiar a la pestaña de login
                    if (getActivity() instanceof AuthActivity) {
                        ((AuthActivity) getActivity()).switchToTab(0);
                    }

                } else {
                    String mensajeError = "Error en el registro";

                    try {
                        if (response.code() == 400 || response.code() == 409) {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e("REGISTER_ERROR", "Error del backend: " + errorBody);

                                if (errorBody.toLowerCase().contains("documento") ||
                                        errorBody.toLowerCase().contains("duplicado") ||
                                        errorBody.toLowerCase().contains("ya existe") ||
                                        errorBody.toLowerCase().contains("already exists") ||
                                        errorBody.toLowerCase().contains("unique constraint")) {

                                    mensajeError = "Este documento ya está registrado";

                                    String docActual = etDNI.getText().toString().trim();
                                    if (!documentosRegistrados.contains(docActual)) {
                                        documentosRegistrados.add(docActual);
                                        Log.d("REGISTER", "Documento agregado a lista: " + docActual);
                                    }

                                    tilDNI.setError("Este documento ya está registrado");
                                    etDNI.requestFocus();
                                } else {
                                    mensajeError = errorBody.length() < 200 ? errorBody :
                                            "Datos inválidos. Verifique la información";
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

    private void limpiarFormulario() {
        etDNI.setText("");
        etNombres.setText("");
        etApellidoPaterno.setText("");
        etApellidoMaterno.setText("");
        etDomicilio.setText("");
        etFechaNacimiento.setText("");
        etClave.setText("");
        etConfirmarClave.setText("");
        etContactoEmergenciaNombre.setText("");
        etNumeroEmergencia.setText("");

        autoTipoDocumento.setText("");
        autoSexo.setText("");

        chkTerminos.setChecked(false);
        chkSeguroSalud.setChecked(false);

        tilDNI.setError(null);
        tilClave.setError(null);
        tilConfirmarClave.setError(null);
    }

    private void configurarTerminosClickeables() {
        String textoCompleto = "Acepto los Términos y Condiciones y las Políticas de privacidad";
        android.text.SpannableString spannableString = new android.text.SpannableString(textoCompleto);

        // Términos y Condiciones (clickeable)
        android.text.style.ClickableSpan clickTerminos = new android.text.style.ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mostrarDialogoTerminos();
            }

            @Override
            public void updateDrawState(android.text.TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.color_primary, null)); // Color del enlace
                ds.setUnderlineText(true);
            }
        };

        // Políticas de privacidad (clickeable)
        android.text.style.ClickableSpan clickPoliticas = new android.text.style.ClickableSpan() {
            @Override
            public void onClick(View widget) {
                mostrarDialogoPoliticas();
            }

            @Override
            public void updateDrawState(android.text.TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.color_primary, null));
                ds.setUnderlineText(true);
            }
        };

        // Aplicar spans a las palabras específicas
        int inicioTerminos = textoCompleto.indexOf("Términos y Condiciones");
        int finTerminos = inicioTerminos + "Términos y Condiciones".length();

        int inicioPoliticas = textoCompleto.indexOf("Políticas de privacidad");
        int finPoliticas = inicioPoliticas + "Políticas de privacidad".length();

        spannableString.setSpan(clickTerminos, inicioTerminos, finTerminos,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickPoliticas, inicioPoliticas, finPoliticas,
                android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        chkTerminos.setText(spannableString);
        chkTerminos.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        chkTerminos.setHighlightColor(android.graphics.Color.TRANSPARENT);
    }

    private void mostrarDialogoTerminos() {
        String terminos = "TÉRMINOS Y CONDICIONES DE USO\n" +
                "Aplicación Móvil Robles Farma\n\n" +

                "Última actualización: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new java.util.Date()) + "\n\n" +

                "1. ACEPTACIÓN DE LOS TÉRMINOS\n" +
                "Al acceder y utilizar la aplicación móvil Robles Farma (en adelante, \"la Aplicación\"), " +
                "usted acepta cumplir con estos Términos y Condiciones. Si no está de acuerdo con alguna " +
                "parte de estos términos, no debe utilizar la Aplicación.\n\n" +

                "2. USO DE LA APLICACIÓN\n" +
                "2.1. La Aplicación permite a los usuarios:\n" +
                "   • Agendar citas médicas\n" +
                "   • Consultar historial médico\n" +
                "   • Comunicarse con personal médico\n" +
                "   • Acceder a resultados de análisis\n\n" +
                "2.2. Usted se compromete a:\n" +
                "   • Proporcionar información veraz y actualizada\n" +
                "   • Mantener la confidencialidad de sus credenciales de acceso\n" +
                "   • No compartir su cuenta con terceros\n" +
                "   • Usar la Aplicación únicamente para fines médicos legítimos\n\n" +

                "3. REGISTRO DE USUARIO\n" +
                "3.1. Para utilizar ciertos servicios, deberá crear una cuenta proporcionando:\n" +
                "   • Nombre completo\n" +
                "   • Número de documento de identidad\n" +
                "   • Fecha de nacimiento\n" +
                "   • Información de contacto\n\n" +
                "3.2. Usted es responsable de mantener actualizada su información personal.\n\n" +

                "4. CITAS MÉDICAS\n" +
                "4.1. Las citas agendadas a través de la Aplicación están sujetas a disponibilidad.\n" +
                "4.2. Debe cancelar o reprogramar citas con al menos 24 horas de anticipación.\n" +
                "4.3. El incumplimiento reiterado puede resultar en restricciones de uso.\n\n" +

                "5. LIMITACIÓN DE RESPONSABILIDAD\n" +
                "La Aplicación es una herramienta de apoyo y no sustituye la consulta médica presencial. " +
                "El personal médico es responsable de los diagnósticos y tratamientos proporcionados.\n\n" +

                "6. MODIFICACIONES\n" +
                "Nos reservamos el derecho de modificar estos términos en cualquier momento. " +
                "Los cambios entrarán en vigor inmediatamente después de su publicación en la Aplicación.\n\n" +

                "7. CONTACTO\n" +
                "Para consultas sobre estos términos, contacte a:\n" +
                "Email: contacto@roblesfarma.com\n" +
                "Teléfono: +51 999 888 777";

        mostrarDialogoTexto("Términos y Condiciones", terminos);
    }

    private void mostrarDialogoPoliticas() {
        String politicas = "POLÍTICA DE PRIVACIDAD\n" +
                "Aplicación Móvil Robles Farma\n\n" +

                "Última actualización: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new java.util.Date()) + "\n\n" +

                "1. INFORMACIÓN QUE RECOPILAMOS\n\n" +

                "1.1. Información Personal:\n" +
                "   • Datos de identificación (nombre, DNI, fecha de nacimiento)\n" +
                "   • Información de contacto (teléfono, dirección)\n" +
                "   • Datos de seguro médico\n" +
                "   • Contacto de emergencia\n\n" +

                "1.2. Información Médica:\n" +
                "   • Historial de citas\n" +
                "   • Resultados de análisis clínicos\n" +
                "   • Diagnósticos y tratamientos\n" +
                "   • Recetas médicas\n" +
                "   • Calificaciones de atención\n\n" +

                "1.3. Información Técnica:\n" +
                "   • Datos de uso de la Aplicación\n" +
                "   • Dirección IP\n" +
                "   • Tipo de dispositivo\n" +
                "   • Sistema operativo\n\n" +

                "2. USO DE LA INFORMACIÓN\n\n" +
                "Utilizamos su información para:\n" +
                "   • Gestionar citas médicas\n" +
                "   • Facilitar la comunicación con el personal médico\n" +
                "   • Mejorar nuestros servicios\n" +
                "   • Enviar notificaciones importantes\n" +
                "   • Cumplir con obligaciones legales\n\n" +

                "3. PROTECCIÓN DE DATOS\n\n" +
                "3.1. Implementamos medidas de seguridad técnicas y organizativas:\n" +
                "   • Encriptación de datos sensibles\n" +
                "   • Autenticación segura\n" +
                "   • Acceso restringido al personal autorizado\n" +
                "   • Copias de seguridad regulares\n" +
                "   • Monitoreo de seguridad 24/7\n\n" +

                "3.2. Sus datos médicos están protegidos bajo la Ley N° 29733 - " +
                "Ley de Protección de Datos Personales del Perú.\n\n" +

                "4. COMPARTIR INFORMACIÓN\n\n" +
                "No compartimos su información con terceros, excepto:\n" +
                "   • Personal médico autorizado para su atención\n" +
                "   • Laboratorios para procesamiento de análisis\n" +
                "   • Autoridades competentes cuando la ley lo requiera\n" +
                "   • Con su consentimiento explícito\n\n" +

                "5. SUS DERECHOS\n\n" +
                "Usted tiene derecho a:\n" +
                "   • Acceder a su información personal\n" +
                "   • Rectificar datos incorrectos\n" +
                "   • Solicitar la eliminación de sus datos\n" +
                "   • Oponerse al procesamiento de datos\n" +
                "   • Portabilidad de datos\n" +
                "   • Revocar consentimientos\n\n" +

                "Para ejercer estos derechos, contacte a:\n" +
                "Email: privacidad@roblesfarma.com\n\n" +

                "6. COOKIES Y TECNOLOGÍAS SIMILARES\n\n" +
                "Utilizamos cookies y tecnologías similares para mejorar su experiencia. " +
                "Puede gestionar sus preferencias desde la configuración de la Aplicación.\n\n" +

                "7. CAMBIOS EN LA POLÍTICA\n\n" +
                "Nos reservamos el derecho de actualizar esta política. Le notificaremos " +
                "sobre cambios significativos a través de la Aplicación.\n\n" +

                "8. MENORES DE EDAD\n\n" +
                "La Aplicación puede ser utilizada por menores de edad bajo supervisión de un tutor legal, " +
                "quien es responsable de la información proporcionada.\n\n" +

                "9. CONTACTO\n\n" +
                "Para consultas sobre esta política, contacte a:\n" +
                "Robles Farma - Departamento de Protección de Datos\n" +
                "Email: privacidad@roblesfarma.com\n" +
                "Teléfono: +51 999 888 777\n" +
                "Dirección: Av. Principal 123, Lambayeque, Perú";

        mostrarDialogoTexto("Política de Privacidad", politicas);
    }

    private void mostrarDialogoTexto(String titulo, String contenido) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle(titulo);

        // Crear ScrollView para contenido largo
        android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
        android.widget.TextView textView = new android.widget.TextView(requireContext());

        textView.setText(contenido);
        textView.setPadding(50, 40, 50, 40);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.black, null));
        textView.setLineSpacing(1.2f, 1.2f);

        scrollView.addView(textView);
        builder.setView(scrollView);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            chkTerminos.setChecked(true);
            dialog.dismiss();
        });

        builder.setNegativeButton("Cerrar", (dialog, which) -> dialog.dismiss());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}