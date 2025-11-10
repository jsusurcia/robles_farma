package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class PacienteResponse {

    @SerializedName("id")
    private int idPaciente;

    @SerializedName("nro_documento")
    private String nroDocumento;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido_paterno")
    private String apellidoPaterno;

    @SerializedName("apellido_materno")
    private String apellidoMaterno;

    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento;

    @SerializedName("sexo")
    private boolean sexo;

    @SerializedName("foto_perfil_url")
    private String fotoPerfilUrl;

    @SerializedName("contacto_emergencia_nombre")
    private String contactoEmergenciaNombre;

    @SerializedName("contacto_emergencia_telefono")
    private String contactoEmergenciaTelefono;

    private String rol;

    // ===========================
    // ✅ Getters y Setters
    // ===========================

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    // 👇 Alias para compatibilidad con LoginStorage y Retrofit
    public String getId() {
        return String.valueOf(idPaciente);
    }

    public void setId(String id) {
        try {
            this.idPaciente = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            this.idPaciente = 0;
        }
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean isSexo() {
        return sexo;
    }

    public void setSexo(boolean sexo) {
        this.sexo = sexo;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }

    public String getContactoEmergenciaNombre() {
        return contactoEmergenciaNombre;
    }

    public void setContactoEmergenciaNombre(String contactoEmergenciaNombre) {
        this.contactoEmergenciaNombre = contactoEmergenciaNombre;
    }

    public String getContactoEmergenciaTelefono() {
        return contactoEmergenciaTelefono;
    }

    public void setContactoEmergenciaTelefono(String contactoEmergenciaTelefono) {
        this.contactoEmergenciaTelefono = contactoEmergenciaTelefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // ✅ Método útil para mostrar nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellidoPaterno + " " + apellidoMaterno;
    }
}
