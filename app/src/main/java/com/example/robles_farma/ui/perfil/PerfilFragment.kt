package com.example.robles_farma.ui.perfil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.robles_farma.R
import com.example.robles_farma.databinding.FragmentPerfilBinding
import com.example.robles_farma.sharedpreferences.LoginStorage
import com.example.robles_farma.ui.auth.AuthActivity

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    // Declara tu LoginStorage
    private lateinit var loginStorage: LoginStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 1. Configura el ViewBinding
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        // 2. Inicializa el LoginStorage
        loginStorage = LoginStorage(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Carga los datos del paciente en la UI
        setupDatosPaciente()

        // 4. Configura los listeners de los botones
        setupClickListeners()
    }

    private fun setupDatosPaciente() {
        val paciente = loginStorage.paciente

        if (paciente != null) {
            // Combina nombre y apellido paterno para el nombre principal
            binding.textViewNombre.text = "${paciente.nombres} ${paciente.apellidoPaterno}"

            binding.textViewEmail.text = "DNI: ${paciente.nroDocumento}"

            // Cargar la imagen de perfil con Glide
            Glide.with(this)
                .load(paciente.fotoPerfilUrl)
                .placeholder(R.drawable.default_user_image) // Imagen mientras carga
                .error(R.drawable.default_user_image)       // Imagen si falla la carga
                .into(binding.imageViewAvatar)

        } else {
            binding.textViewNombre.text = "Usuario Invitado"
            binding.textViewEmail.text = "No hay datos"
        }
    }

    private fun setupClickListeners() {
        // 5. Navegación para "Editar Perfil"
        binding.textViewEditarPerfil.setOnClickListener {
            // Usamos la 'action' que ya definiste en mobile_navigation.xml
            findNavController().navigate(R.id.action_navigation_perfil_to_editarPerfilFragment)
        }

        // 6. Lógica para "Cerrar Sesión"
        binding.textViewCerrarSesion.setOnClickListener {
            mostrarDialogoDeCierreSesion()
        }

        // (Puedes añadir listeners para 'Ajustes' aquí si lo necesitas)
    }

    private fun mostrarDialogoDeCierreSesion() {
        // Usamos un AlertDialog para confirmar
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí, cerrar") { dialog, _ ->
                // 1. Limpia los datos de SharedPreferences
                loginStorage.clearLoginCredentials()

                // 2. Navega de vuelta a AuthActivity (Login)
                val intent = Intent(requireActivity(), AuthActivity::class.java)

                // Estas 'flags' son MUY importantes.
                // Limpian la pila de Activities para que el usuario
                // no pueda "volver" al MainActivity con el botón 'atrás'.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                // Finaliza la Activity actual (MainActivity)
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}