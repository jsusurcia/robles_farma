package com.example.robles_farma.retrofit;

import com.example.robles_farma.request.LoginRequest;
import com.example.robles_farma.request.RegisterRequest;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.LoginResponse;
import com.example.robles_farma.response.PacienteResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    // 🔹 Login paciente
    @POST("pacientes/login")
    Call<ItemResponse<LoginResponse>> login(@Body LoginRequest request);

    // 🔹 Registrar paciente
    @POST("pacientes/")
    Call<ItemResponse<PacienteResponse>> registerPaciente(@Body RegisterRequest request);
}
