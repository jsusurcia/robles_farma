package com.example.robles_farma.retrofit;

import com.example.robles_farma.request.LoginRequest;
import com.example.robles_farma.request.RegisterRequest;
import com.example.robles_farma.response.BusquedaEspecialidadResponse;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.LoginResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.response.EspecialidadResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ApiService {

    // 🔹 Login paciente
    @POST("pacientes/login")
    Call<ItemResponse<LoginResponse>> login(@Body LoginRequest request);

    // 🔹 Registrar paciente
    @POST("pacientes/")
    Call<ItemResponse<PacienteResponse>> registerPaciente(@Body RegisterRequest request);

    @GET("especialidades/")
    Call<EspecialidadResponse> getEspecialidades();

    @GET("especialidades/{busqueda}")
    Call<BusquedaEspecialidadResponse> getBusquedaEspecialidad(@Path("busqueda") String busqueda);
}