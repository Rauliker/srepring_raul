package com.example.example.dto;

public class RespuestaDto {

    private String message;

    public RespuestaDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
