package com.example.robles_farma.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.robles_farma.databinding.DialogCalificacionBinding

import android.util.Log
import com.example.robles_farma.model.CalificacionData
import com.example.robles_farma.request.CalificacionCreateRequest
import com.example.robles_farma.response.CalificacionResponse
import com.example.robles_farma.retrofit.ApiService
import com.example.robles_farma.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalificacionDialog : DialogFragment() {

    private var _binding: DialogCalificacionBinding? = null
    private val binding get() = _binding!!
    private var citaId: String? = null

    private val apiService: ApiService = RetrofitClient.createService()

    companion object {
        const val TAG = "CalificacionDialog"

        // Método estático para crear una nueva instancia con argumentos
        fun newInstance(citaId: String): CalificacionDialog {
            val args = Bundle()
            args.putString("cita_id", citaId)
            val fragment = CalificacionDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        citaId = arguments?.getString("cita_id")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCalificacionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hacer el fondo transparente para que se vea redondeado
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.btnEnviarCalificacion.setOnClickListener {
            val estrellas = binding.ratingBar.rating
            val comentario = binding.etComentario.text.toString()

            if (estrellas == 0f) {
                Toast.makeText(context, "Por favor selecciona una calificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarCalificacionAlBackend(estrellas.toInt(), comentario)
        }
    }

    private fun enviarCalificacionAlBackend(puntuacion: Int, comentario: String) {
        // 1. Validar que tengamos el ID de la cita
        val idCitaInt = citaId?.toIntOrNull()
        if (idCitaInt == null) {
            Toast.makeText(context, "Error: ID de cita no válido", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Mostrar carga (deshabilitar botón para evitar doble clic)
        binding.btnEnviarCalificacion.isEnabled = false
        binding.btnEnviarCalificacion.text = "Enviando..."

        // 3. Preparar el Request
        val request = CalificacionCreateRequest(
            idCita = idCitaInt,
            puntuacion = puntuacion,
            comentario = if (comentario.isNotEmpty()) comentario else null
        )

        // 4. Obtener el servicio (Retrofit)
        // Nota: Como ya tienes AuthInterceptor, el token se añade solo.
        //val apiService = RetrofitClient.createService(ApiService::class.java)

        // 5. Hacer la llamada
        apiService.registrarCalificacion(request).enqueue(object : Callback<CalificacionResponse> {
            override fun onResponse(
                call: Call<CalificacionResponse>,
                response: Response<CalificacionResponse>
            ) {
                // Reactivar botón por si acaso (aunque cerraremos el diálogo)
                if (isAdded) { // Verificamos si el fragment sigue vivo
                    binding.btnEnviarCalificacion.isEnabled = true
                    binding.btnEnviarCalificacion.text = "Enviar calificación"
                }

                if (response.isSuccessful) {
                    val calificacionResp = response.body()
                    if (calificacionResp != null && calificacionResp.status == "success") {
                        Toast.makeText(context, "¡Gracias por tu opinión!", Toast.LENGTH_LONG).show()
                        dismiss() // CERRAMOS EL DIÁLOGO CON ÉXITO
                    } else {
                        Toast.makeText(context, "Error: ${calificacionResp?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Manejo de errores (ej. 409 Conflict si ya calificó)
                    if (response.code() == 409) {
                        Toast.makeText(context, "Ya calificaste esta cita anteriormente.", Toast.LENGTH_LONG).show()
                        dismiss()
                    } else {
                        Toast.makeText(context, "Error al enviar (${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<CalificacionResponse>, t: Throwable) {
                if (isAdded) {
                    binding.btnEnviarCalificacion.isEnabled = true
                    binding.btnEnviarCalificacion.text = "Enviar calificación"
                    Toast.makeText(context, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("CalificacionDialog", "Error API: ${t.message}")
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}