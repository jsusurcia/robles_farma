package com.example.robles_farma.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.robles_farma.model.EspecialidadData
import com.example.robles_farma.response.EspecialidadResponse
import com.example.robles_farma.retrofit.ApiService
import com.example.robles_farma.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.robles_farma.model.BusquedaEspecialidadData
import com.example.robles_farma.response.BusquedaEspecialidadResponse
import java.io.IOException
import com.example.robles_farma.sharedpreferences.LoginStorage



class EspMasBuscadasVM(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitClient.createService()

    // =============== TOP 6 MEJORES ESPECIALIDADES OñO ========================
    // LiveData para guardar la lista de especialidades
    // El Fragment observará este objeto
    private val _especialidades = MutableLiveData<List<EspecialidadData>>()
    val especialidades: LiveData<List<EspecialidadData>> get() = _especialidades

    // LiveData para el estado de carga (para mostrar un ProgressBar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData para mensajes de error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage


    // ======== CAMPOS DE BÚSQUEDA BRUHHHH ================
    // --- LiveData para los resultados de BÚSQUEDA ---
    private val _searchResults = MutableLiveData<List<BusquedaEspecialidadData>>()
    val searchResults: LiveData<List<BusquedaEspecialidadData>> get() = _searchResults

    // --- Referencia a la llamada de búsqueda para poder cancelarla ---
    private var searchCall: Call<BusquedaEspecialidadResponse>? = null


    fun listarEspecialidades() {
        // Marcamos que está cargando
        _isLoading.value = true

        val context = getApplication<Application>().applicationContext
        val token = LoginStorage.getToken(context)

        // 3. VALIDAR EL TOKEN
        if (token == null) {
            _errorMessage.value = "Error: No hay sesión activa."
            _isLoading.value = false
            Log.e("ViewModel", "Token es nulo, no se puede llamar a la API.")
            return // Detener la ejecución si no hay token
        }

        val authToken = "Bearer $token"

        apiService.getEspecialidadesMasSolicitadas(authToken).enqueue(object : Callback<EspecialidadResponse> {
            override fun onResponse(
                call: Call<EspecialidadResponse>,
                response: Response<EspecialidadResponse>
            ) {
                // terminó la carga
                _isLoading.value = false
                if (response.isSuccessful) {
                    _especialidades.value = response.body()?.data
                } else {
                    // Manejar errores de autenticación (ej. 401, 403)
                    if (response.code() == 401 || response.code() == 403) {
                        _errorMessage.value = "Error de autorización. Inicie sesión de nuevo."
                        Log.w("ViewModel", "Error 401/403: Token inválido o expirado.")
                        // Opcional: limpiando el token
                        LoginStorage(context).clearLoginCredentials()
                    } else {
                        _errorMessage.value = "Error: ${response.code()}"
                    }
                    Log.e("ViewModel", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<EspecialidadResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Fallo en la conexión: ${t.message}"
                Log.e("ViewModel", "Fallo de Retrofit: ${t.message}")
            }
        })
    }

    // --- Para buscar especialidades ---
    fun buscarEspecialidades(query: String) {
        Log.d("ViewModel", "Iniciando búsqueda con query: \"$query\"")

        // Si la búsqueda está vacía o es muy corta, limpia los resultados
        if (query.length < 2) {
            Log.d("ViewModel", "Query demasiado corta (${query.length} caracteres), limpiando resultados.")
            _searchResults.value = emptyList()
            return
        }

        searchCall?.cancel()

        val context = getApplication<Application>().applicationContext
        val token = LoginStorage.getToken(context)

        if (token == null) {
            Log.e("ViewModel", "Token es nulo, no se puede buscar.")
            // Opcional: mostrar error en la UI de búsqueda
            _searchResults.value = emptyList() // Limpia resultados
            return
        }

        val authToken = "Bearer $token"

        //  Cancela cualquier búsqueda anterior que aún esté en progreso
        searchCall?.cancel()

        //  Crea la nueva llamada
        Log.d("ViewModel", "Creando nueva llamada a la API para: \"$query\"")
        searchCall = apiService.getBusquedaEspecialidad(query, authToken)
        searchCall?.enqueue(object : Callback<BusquedaEspecialidadResponse> {
            override fun onResponse(
                call: Call<BusquedaEspecialidadResponse>,
                response: Response<BusquedaEspecialidadResponse>
            ) {
                Log.d("ViewModel", "Respuesta recibida de la API (código: ${response.code()})")
                if (response.isSuccessful) {
                    // Actualiza el LiveData con los nuevos resultados

                    _searchResults.value = response.body()?.data
                    Log.d("ViewModel", "Búsqueda exitosa. Resultados encontrados: ${_searchResults.value}")
                } else {
                    Log.w("ViewModel", "Respuesta no exitosa: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BusquedaEspecialidadResponse>, t: Throwable) {
                // Si la llamada fue cancelada, no hagas nada
                if (t is IOException && t.message == "Canceled") {
                    Log.d("ViewModel", "Búsqueda cancelada")
                } else {
                    // Otro error de red
                    Log.e("ViewModel", "Fallo en búsqueda: ${t.message}")
                }
            }
        })
    }

    // --- Cancelar la llamada si el ViewModel se destruye ---
    override fun onCleared() {
        super.onCleared()
        searchCall?.cancel()
    }
}