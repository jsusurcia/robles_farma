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


class EspMasBuscadasVM(application: Application) : AndroidViewModel(application) {
    private val apiService: ApiService = RetrofitClient.createService()

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

    fun listarEspecialidades() {
        // Marcamos que está cargando
        _isLoading.value = true

        apiService.getEspecialidades().enqueue(object : Callback<EspecialidadResponse> {
            override fun onResponse(
                call: Call<EspecialidadResponse>,
                response: Response<EspecialidadResponse>
            ) {
                // terminó la carga
                _isLoading.value = false

                if (response.isSuccessful) {
                    // Si la respuesta fue exitosa, actualizamos el LiveData
                    // con la lista de datos (response.body()?.data)
                    _especialidades.value = response.body()?.data
                } else {
                    // Si hubo un error en el servidor (ej: 404, 500)
                    _errorMessage.value = "Error: ${response.code()}"
                    Log.e("ViewModel", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<EspecialidadResponse>, t: Throwable) {
                // terminó la carga
                _isLoading.value = false
                // Si hubo un error de red o al procesar la respuesta
                _errorMessage.value = "Fallo en la conexión: ${t.message}"
                Log.e("ViewModel", "Fallo de Retrofit: ${t.message}")
            }
        })
    }
}