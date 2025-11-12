package com.example.robles_farma.retrofit;

import com.example.robles_farma.request.LoginRequest;
import com.example.robles_farma.request.PacienteUpdatePassRequest;
import com.example.robles_farma.request.PacienteUpdateRequest;
import com.example.robles_farma.request.RegisterRequest;
import com.example.robles_farma.response.BusquedaEspecialidadResponse;
import com.example.robles_farma.response.CancelarCitaResponse;
import com.example.robles_farma.response.CitasPacienteResponse;
import com.example.robles_farma.response.FotoUploadResponse;
import com.example.robles_farma.response.ItemResponse;
import com.example.robles_farma.response.LoginResponse;
import com.example.robles_farma.response.PacienteResponse;
import com.example.robles_farma.response.EspecialidadResponse;
import com.example.robles_farma.response.PacienteUpdatePassResponse;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;


public interface ApiService {

    // 🔹 Login paciente
    @POST("pacientes/login")
    Call<ItemResponse<LoginResponse>> login(@Body LoginRequest request);

    // 🔹 Registrar paciente
    @POST("pacientes/")
    Call<ItemResponse<PacienteResponse>> registerPaciente(@Body RegisterRequest request);

    @GET("especialidades/")
    Call<EspecialidadResponse> getEspecialidades(
            @Header("Authorization") String token
    );

    @GET("especialidades/{busqueda}")
    Call<BusquedaEspecialidadResponse> getBusquedaEspecialidad(
            @Path("busqueda") String busqueda,
            @Header("Authorization") String token
    );

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

    // 🔹 Cancelar una cita
    @POST("cita/{id_cita}/cancelar")
    Call<CancelarCitaResponse> cancelarCita(@Path("id_cita") int idCita);

    // 🔹 Reprogramar una cita


    @Streaming
    @GET("pacientes/foto")
    Call<ResponseBody> getFotoPerfil();

    @Multipart
    @PUT("pacientes/update_foto")
    Call<FotoUploadResponse> updateFotoPerfil(@Part MultipartBody.Part file);
}