package com.example.robles_farma.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.robles_farma.request.PacienteUpdatePassRequest
import com.example.robles_farma.request.PacienteUpdateRequest

import com.example.robles_farma.response.ItemResponse
import com.example.robles_farma.response.PacienteResponse
import com.example.robles_farma.response.PacienteUpdatePassResponse

import com.example.robles_farma.retrofit.ApiService
import com.example.robles_farma.retrofit.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarPerfilVM(application: Application) : AndroidViewModel(application) {

    private val apiService: ApiService = RetrofitClient.createService()

    // LiveData para estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // LiveData para resultado de ACTUALIZAR DATOS
    private val _updateResult = MutableLiveData<ItemResponse<PacienteResponse>>()
    val updateResult: LiveData<ItemResponse<PacienteResponse>> get() = _updateResult

    // LiveData para resultado de CAMBIAR CLAVE
    private val _passwordResult = MutableLiveData<PacienteUpdatePassResponse>()
    val passwordResult: LiveData<PacienteUpdatePassResponse> get() = _passwordResult

    // LiveData para mensajes de error
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun actualizarDatos(request: PacienteUpdateRequest) {
        _isLoading.value = true
        apiService.updatePaciente(request).enqueue(object : Callback<ItemResponse<PacienteResponse>> {
            override fun onResponse(
                call: Call<ItemResponse<PacienteResponse>>,
                response: Response<ItemResponse<PacienteResponse>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _updateResult.value = response.body()
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ItemResponse<PacienteResponse>>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Fallo de red: ${t.message}"
            }
        })
    }

    fun actualizarPassword(request: PacienteUpdatePassRequest) {
        _isLoading.value = true
        apiService.updatePacientePassword(request).enqueue(object : Callback<PacienteUpdatePassResponse> {
            override fun onResponse(
                call: Call<PacienteUpdatePassResponse>,
                response: Response<PacienteUpdatePassResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful() && response.body() != null) {
                    _passwordResult.value = response.body()
                } else {
                    // Manejo de errores específicos de la API (clave incorrecta, etc.)
                    try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val jsonError = JSONObject(errorBody)
                            _error.value = jsonError.getString("detail") // Extrae el mensaje de "detail"
                        } else {
                            _error.value = "Error ${response.code()}"
                        }
                    } catch (e: Exception) {
                        _error.value = "Error al procesar respuesta: ${response.code()}"
                    }
                }
            }

            override fun onFailure(call: Call<PacienteUpdatePassResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Fallo de red: ${t.message}"
            }
        })
    }
}