package com.example.robles_farma.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {

    @SerializedName("nro_documento")
    private String nroDocumento;
    private String clave;
    private String nombres;
    @SerializedName("apellido_paterno")
    private String apellidoPaterno;
    @SerializedName("apellido_materno")
    private String apellidoMaterno;
    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento;
    private boolean sexo;
    @SerializedName("id_tipo_documento")
    private int idTipoDocumento;
    @SerializedName("foto_perfil_url")
    private String fotoPerfilUrl;
    @SerializedName("contacto_emergencia_nombre")
    private String contactoEmergenciaNombre;
    @SerializedName("contacto_emergencia_telefono")
    private String contactoEmergenciaTelefono;


    // Constructor, getters y setters

    public RegisterRequest(String nroDocumento, String clave, String nombres, String apellidoPaterno, String apellidoMaterno, String fechaNacimiento, boolean sexo, int idTipoDocumento, String fotoPerfilUrl, String contactoEmergenciaNombre, String contactoEmergenciaTelefono) {
        this.nroDocumento = nroDocumento;
        this.clave = clave;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.idTipoDocumento = idTipoDocumento;
        this.fotoPerfilUrl = fotoPerfilUrl;
        this.contactoEmergenciaNombre = contactoEmergenciaNombre;
        this.contactoEmergenciaTelefono = contactoEmergenciaTelefono;
    }

    // Getters y Setters para todos los campos...
}
