package com.example.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginUsuario {

    @JsonProperty("username")
    private String nombreUsuario;

    private String password;

    // Getters and Setters
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
