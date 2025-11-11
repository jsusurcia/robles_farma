package com.example.robles_farma.ui.perfil

import android.content.Context
import android.content.Intent
import android.database.Cursor
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
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.robles_farma.retrofit.RetrofitClient
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.robles_farma.retrofit.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import com.example.robles_farma.response.FotoUploadResponse
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLConnection

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    // Declara tu LoginStorage
    private lateinit var loginStorage: LoginStorage

    // Declara el ActivityResultLauncher para seleccionar imágenes
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Se seleccionó una imagen, ahora la subimos
            uploadFotoToServer(it)
        }
    }

    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 1. Configura el ViewBinding
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        // 2. Inicializa el LoginStorage
        loginStorage = LoginStorage(requireContext())

        apiService = RetrofitClient.createService()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3. Carga los datos del paciente en la UI
        setupDatosPaciente()

        // 4. Configura los listeners de los botones
        setupClickListeners()

        binding.imageViewAvatar.setOnClickListener {
            mostrarDialogoOpcionesFoto()
        }
    }

    private fun setupDatosPaciente() {
        val paciente = loginStorage.paciente
        val token = loginStorage.token

        if (paciente != null && token != null) {
            // Combina nombre y apellido paterno para el nombre principal
            binding.textViewNombre.text = "${paciente.nombres} ${paciente.apellidoPaterno} ${paciente.apellidoMaterno}"
            binding.textViewEmail.text = "DNI: ${paciente.nroDocumento}"

            val fotoUrl = RetrofitClient.URL_API_SERVICE + "pacientes/foto"

            val headers = LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            val glideUrl = GlideUrl(fotoUrl, headers)

            // Cargar la imagen de perfil con Glide
            Glide.with(this)
                .load(glideUrl)
                .placeholder(R.drawable.default_user_image)
                .error(R.drawable.default_user_image)
                .skipMemoryCache(true) // No guardar en caché de memoria
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE) // No guardar en caché de disco
                .into(binding.imageViewAvatar)

        } else {
            binding.textViewNombre.text = "Usuario Invitado"
            binding.textViewEmail.text = "No hay datos"
            binding.imageViewAvatar.setImageResource(R.drawable.default_user_image)
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
        binding.textViewAjustes.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_perfil_to_navigation_configuracion)
        }
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

    private fun mostrarDialogoOpcionesFoto() {
        val opciones = arrayOf("Ver foto de perfil", "Actualizar foto de perfil")
        AlertDialog.Builder(requireContext())
            .setTitle("Opciones de foto de perfil")
            .setItems(opciones) { dialog, which ->
                when (which) {
                    0 -> { // Ver foto de perfil
                        // Navega a un Fragment/Activity de visualización de imagen
                        // Aquí deberías pasar la URL de la imagen si tu "ver" fragment la necesita
                        val bundle = Bundle().apply {
                            putString("fotoUrl", RetrofitClient.URL_API_SERVICE + "pacientes/foto")
                            putString("token", loginStorage.token) // Pasa el token si la vista también lo necesita
                        }
                        findNavController().navigate(R.id.action_navigation_perfil_to_verFotoFragment, bundle)
                    }
                    1 -> { // Actualizar foto de perfil
                        selectImageLauncher.launch("image/*") // Abre la galería
                    }
                }
            }
            .show()
    }

    private fun uploadFotoToServer(fileUri: Uri) {
        val token = loginStorage.token
        if (token == null) {
            Toast.makeText(requireContext(), "Error: No se encontró token.", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Obtener un nombre de archivo desde el ContentResolver
        val fileName = getFileName(requireContext(), fileUri) ?: "temp_image.jpg"

        // 2. Crear un archivo temporal en la caché de la app
        val tempFile = File(requireContext().cacheDir, fileName)

        // 3. Copiar el contenido del Uri al archivo temporal
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(fileUri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al procesar el archivo.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            return
        }

        // 4. Detectar el MIME type real y crear el RequestBody
        val mimeType = requireContext().contentResolver.getType(fileUri)
            ?: URLConnection.guessContentTypeFromName(tempFile.name)
            ?: "image/jpeg" // valor por defecto

        Log.d("UploadDebug", "MIME type detectado: $mimeType")

        val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

        // 5. Llamar a la API (esto es lo mismo que ya tenías)
        apiService.updateFotoPerfil(body).enqueue(object : Callback<FotoUploadResponse> {
            override fun onResponse(call: Call<FotoUploadResponse>, response: Response<FotoUploadResponse>) {
                tempFile.delete() // Borra el archivo temporal después de la subida
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Foto actualizada exitosamente", Toast.LENGTH_SHORT).show()
                    setupDatosPaciente() // Recarga la imagen
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("PerfilFragment", "Error al subir foto: $errorBody")
                    Toast.makeText(requireContext(), "Error al subir foto: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<FotoUploadResponse>, t: Throwable) {
                tempFile.delete() // Borra el archivo temporal también en caso de fallo de red
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // --- NUEVA FUNCIÓN AUXILIAR ---
    // Reemplaza a getPathFromUri. Obtiene el nombre del archivo.
    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}