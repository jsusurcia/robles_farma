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

    private var currentPhotoUrl: String? = null

    // Declara el ActivityResultLauncher para seleccionar imágenes
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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
            binding.textViewNombre.text =
                "${paciente.nombres} ${paciente.apellidoPaterno} ${paciente.apellidoMaterno}"
            binding.textViewEmail.text = "DNI: ${paciente.nroDocumento}"

            // 2. LLAMADA A LA API PARA OBTENER LA URL DE CLOUDINARY
            // Mostramos un placeholder mientras carga
            binding.imageViewAvatar.setImageResource(R.drawable.default_user_image)

            apiService.getFotoPerfil().enqueue(object : Callback<FotoUploadResponse> {
                override fun onResponse(
                    call: Call<FotoUploadResponse>,
                    response: Response<FotoUploadResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val urlCloudinary = response.body()!!.url

                        // Validar que venga una URL real
                        if (!urlCloudinary.isNullOrEmpty()) {
                            currentPhotoUrl = urlCloudinary

                            // 3. CARGAR CON GLIDE USANDO LA URL DE CLOUDINARY
                            Glide.with(requireContext())
                                .load(currentPhotoUrl)
                                .placeholder(R.drawable.default_user_image)
                                .error(R.drawable.default_user_image)
                                .circleCrop() // Opcional: para que se vea redonda
                                .into(binding.imageViewAvatar)
                        }
                    } else {
                        // Si el backend responde 404 (sin foto), dejamos la default
                        Log.e("PerfilFragment", "No se encontró foto o error en respuesta")
                    }
                }

                override fun onFailure(call: Call<FotoUploadResponse>, t: Throwable) {
                    Log.e("PerfilFragment", "Error de red al pedir foto: ${t.message}")
                }
            })

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

                        // Verificamos si tenemos una URL válida
                        if (currentPhotoUrl != null) {
                            val bundle = Bundle().apply {
                                // Pasamos la URL de Cloudinary DIRECTAMENTE
                                putString("fotoUrl", currentPhotoUrl)
                                // YA NO necesitas pasar el token, la URL de Cloudinary es pública
                            }
                            findNavController().navigate(
                                R.id.action_navigation_perfil_to_verFotoFragment,
                                bundle
                            )
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "No hay foto para mostrar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    1 -> { // Actualizar foto
                        selectImageLauncher.launch("image/*")
                    }
                }
            }
            .show()
    }

    private fun uploadFotoToServer(fileUri: Uri) {
        val token = loginStorage.token
        if (token == null) {
            Toast.makeText(requireContext(), "Error: No se encontró token.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // 1. Obtener un nombre de archivo desde el ContentResolver
        val fileName = getFileName(requireContext(), fileUri) ?: "temp_image.jpg"

        // 2. Crear un archivo temporal en la caché de la app
        val tempFile = File(requireContext().cacheDir, fileName)

        // 3. Copiar el contenido del Uri al archivo temporal
        try {
            val inputStream: InputStream? =
                requireContext().contentResolver.openInputStream(fileUri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al procesar el archivo.", Toast.LENGTH_SHORT)
                .show()
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
            override fun onResponse(
                call: Call<FotoUploadResponse>,
                response: Response<FotoUploadResponse>
            ) {
                tempFile.delete() // Borra el archivo temporal después de la subida
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Foto actualizada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    setupDatosPaciente() // Recarga la imagen
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("PerfilFragment", "Error al subir foto: $errorBody")
                    Toast.makeText(
                        requireContext(),
                        "Error al subir foto: $errorBody",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<FotoUploadResponse>, t: Throwable) {
                tempFile.delete() // Borra el archivo temporal también en caso de fallo de red
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_LONG)
                    .show()
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