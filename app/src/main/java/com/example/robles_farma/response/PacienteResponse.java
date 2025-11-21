package com.example.robles_farma.response;

import com.google.gson.annotations.SerializedName;

public class PacienteResponse {

    @SerializedName("id_paciente")
    private int idPaciente;

    @SerializedName("nro_documento")
    private String nroDocumento;

    private String nombres;

    @SerializedName("apellido_paterno")
    private String apellidoPaterno;

    @SerializedName("apellido_materno")
    private String apellidoMaterno;

    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento;

    @SerializedName("sexo")
    private boolean sexo;

    @SerializedName("estado")
    private boolean estado;

    @SerializedName("foto_perfil_url")
    private String fotoPerfilUrl;

    @SerializedName("contacto_emergencia_nombre")
    private String contactoEmergenciaNombre;

    @SerializedName("contacto_emergencia_telefono")
    private String contactoEmergenciaTelefono;

    @SerializedName("id_tipo_documento")
    private int idTipoDocumento;

    @SerializedName("fecha_creacion")
    private String fechaCreacion;

    @SerializedName("fecha_modificacion")
    private String fechaModificacion;

    @SerializedName("es_asegurado")
    private boolean esAsegurado;

    private String rol;  // Este campo puede no venir del backend

    // ===== GETTERS Y SETTERS =====

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
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


    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
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


    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }


    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }


    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    // NUEVO
    public boolean isEsAsegurado() {
        return esAsegurado;
    }

    public void setEsAsegurado(boolean esAsegurado) {
        this.esAsegurado = esAsegurado;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}