package com.example.robles_farma.retrofit;

import com.example.robles_farma.model.CitasData;
import com.example.robles_farma.request.LoginRequest;
import com.example.robles_farma.request.PacienteUpdatePassRequest;
import com.example.robles_farma.request.PacienteUpdateRequest;
import com.example.robles_farma.request.RegisterRequest;
import com.example.robles_farma.response.BusquedaEspecialidadResponse;
import com.example.robles_farma.response.CitasPacienteResponse;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.LoginResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.response.EspecialidadResponse;
import com.example.robles_farma.response.PacienteUpdatePassResponse;
import com.example.robles_farma.response.PacienteUpdateResponse;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
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

    @PUT("pacientes/update")
    Call<ItemResponse<PacienteResponse>> updatePaciente(@Body PacienteUpdateRequest request);

    @PUT("pacientes/update_password")
    Call<PacienteUpdatePassResponse> updatePacientePassword(@Body PacienteUpdatePassRequest request);

    // 🔹 Obtener citas proximas de un paciente
    @GET("cita/proximas_paciente/{id_paciente}")
    Call<CitasPacienteResponse> getCitasProximas(@Path("id_paciente") int idPaciente);

    // 🔹 Obtener citas pasadas de un paciente
    @GET("cita/pasadas_paciente/{id_paciente}")
    Call<CitasPacienteResponse> getCitasPasadas(@Path("id_paciente") int idPaciente);
}