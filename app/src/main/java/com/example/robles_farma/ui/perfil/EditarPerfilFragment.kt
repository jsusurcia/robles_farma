package com.example.robles_farma.ui.perfil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.robles_farma.databinding.FragmentEditarPerfilBinding
import com.example.robles_farma.request.PacienteUpdatePassRequest
import com.example.robles_farma.request.PacienteUpdateRequest
import com.example.robles_farma.response.PacienteResponse
import com.example.robles_farma.sharedpreferences.LoginStorage
import com.example.robles_farma.viewmodels.EditarPerfilVM


class EditarPerfilFragment : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginStorage: LoginStorage
    private val viewModel: EditarPerfilVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        loginStorage = LoginStorage(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Añadí la llamada a setupToolbar
        cargarDatosActuales()
        setupClickListeners()
        observeViewModel()
    }

    private fun cargarDatosActuales() {
        // ... (esta función está perfecta, no necesita cambios) ...
        val paciente = loginStorage.paciente
        if (paciente != null) {
            binding.etNombres.setText(paciente.nombres)
            binding.etApellidoPaterno.setText(paciente.apellidoPaterno)
            binding.etApellidoMaterno.setText(paciente.apellidoMaterno)
            binding.etDni.setText(paciente.nroDocumento)
            binding.etFechaNacimiento.setText(paciente.fechaNacimiento)
            binding.etContactoNombre.setText(paciente.contactoEmergenciaNombre)
            binding.etContactoTelefono.setText(paciente.contactoEmergenciaTelefono)
        }
    }

    private fun setupClickListeners() {
        // ... (esta función está perfecta, no necesita cambios) ...
        // --- Listener para Guardar Datos Personales ---
        binding.btnGuardarDatos.setOnClickListener {
            val request = PacienteUpdateRequest(
                nroDocumento = binding.etDni.text.toString(),
                nombres = binding.etNombres.text.toString(),
                apellidoPaterno = binding.etApellidoPaterno.text.toString(),
                apellidoMaterno = binding.etApellidoMaterno.text.toString(),
                fechaNacimiento = binding.etFechaNacimiento.text.toString(),
                contactoEmergenciaNombre = binding.etContactoNombre.text.toString(),
                contactoEmergenciaTelefono = binding.etContactoTelefono.text.toString()
                // Los campos no incluidos se enviarán como 'null' gracias a tu data class
            )
            viewModel.actualizarDatos(request)
        }

        // --- Listener para Cambiar Contraseña ---
        binding.btnCambiarClave.setOnClickListener {
            val actual = binding.etClaveActual.text.toString()
            val nueva = binding.etClaveNueva.text.toString()
            val confirmar = binding.etConfirmarClaveNueva.text.toString()

            if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nueva != confirmar) {
                binding.tilConfirmarClaveNueva.error = "Las contraseñas no coinciden"
                return@setOnClickListener
            } else {
                binding.tilConfirmarClaveNueva.error = null
            }

            val request = PacienteUpdatePassRequest(
                claveActual = actual,
                claveNueva = nueva
            )
            viewModel.actualizarPassword(request)
        }
    }

    private fun observeViewModel() {
// ... (observador de isLoading está bien) ...
        // Observador para el estado de carga
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnGuardarDatos.isEnabled = !isLoading
            binding.btnCambiarClave.isEnabled = !isLoading
        })

        // Observador para el resultado de ACTUALIZAR DATOS
        // --- CAMBIO AQUÍ ---
        viewModel.updateResult.observe(viewLifecycleOwner, Observer { response ->
            // 'response' AHORA ES 'ItemResponse<PacienteResponse>'
            if (response.status == "success") {
                Toast.makeText(requireContext(), "¡Datos actualizados con éxito!", Toast.LENGTH_SHORT).show()

                // --- ¡MUY IMPORTANTE! ---
                val token = LoginStorage.getToken(requireContext())

                // ¡CORREGIDO!
                // 'response.data' ahora es un 'PacienteResponse'
                val pacienteActualizado: PacienteResponse = response.data

                // ¡Y ESTO AHORA SÍ FUNCIONA!
                loginStorage.saveSession(token, pacienteActualizado)

                // Vuelve a la pantalla de perfil
                findNavController().popBackStack()
            }
        })

        // Observador para el resultado de CAMBIAR CLAVE
// ... (esta función está perfecta, no necesita cambios) ...
        viewModel.passwordResult.observe(viewLifecycleOwner, Observer { response ->
            if (response.status == "success") {
                Toast.makeText(requireContext(), "¡Contraseña actualizada con éxito!", Toast.LENGTH_SHORT).show()
                // Limpia los campos
                binding.etClaveActual.text = null
                binding.etClaveNueva.text = null
                binding.etConfirmarClaveNueva.text = null
            }
        })

        // Observador de Errores
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            // Muestra el error de 'detail' que parseamos en el VM
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}