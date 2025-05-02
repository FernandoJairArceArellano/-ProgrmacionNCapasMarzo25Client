package com.Digis01.FArceProgramacionNCapas.ML;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

public class Usuario {

    private int IdUsuario;
    @NotNull
    @NotEmpty(message = "Campo vacio")
    @Size(min = 3, max = 50, message = "Entre 3 y 50 carateres")
    private String Nombre;

    @NotNull
    @NotEmpty(message = "Ingresa Apellido Paterno")
    @Size(min = 4, max = 20, message = "Entre 4 y 20 caracteres")
    private String ApellidoPaterno;
    private String ApellidoMaterno;

    @NotNull(message = "No se permiten nulos UserName")
    private String Username;

    @NotNull
    @NotEmpty(message = "Ingrese un Correo")
    @Email
    private String Email;
    @NotNull(message = "Ingresa una Contraseña")
    private String Password;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date FNacimiento;
    private char Sexo;
    private String Telefono;
    private String NCelular;
    @Size(min = 18, max = 18, message = "Ingrese un Curp valido")
    private String CURP;
    private int Status;
    private String Imagen;

    public Rol Rol; // Propiedades de Navegacion a Rol

    public int getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(int IdUsuario) {
        this.IdUsuario = IdUsuario;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getApellidoPaterno() {
        return ApellidoPaterno;
    }

    public void setApellidoPaterno(String ApellidoPaterno) {
        this.ApellidoPaterno = ApellidoPaterno;
    }

    public String getApellidoMaterno() {
        return ApellidoMaterno;
    }

    public void setApellidoMaterno(String ApellidoMaterno) {
        this.ApellidoMaterno = ApellidoMaterno;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public Date getFNacimiento() {
        return FNacimiento;
    }

    public void setFNacimiento(Date FNacimiento) {
        this.FNacimiento = FNacimiento;
    }

    public char getSexo() {
        return Sexo;
    }

    public void setSexo(char Sexo) {
        this.Sexo = Sexo;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getNCelular() {
        return NCelular;
    }

    public void setNCelular(String NCelular) {
        this.NCelular = NCelular;
    }

    public String getCURP() {
        return CURP;
    }

    public void setCURP(String CURP) {
        this.CURP = CURP;
    }

    public Rol getRol() {
        return Rol;
    }

    public void setRol(Rol Rol) {
        this.Rol = Rol;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String Imagen) {
        this.Imagen = Imagen;
    }

}
