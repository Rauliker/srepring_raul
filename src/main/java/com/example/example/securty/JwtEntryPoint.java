package com.example.example.securty;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    /**
     * Método que se ejecuta cuando se produce un error de autenticación.
     *
     * @param request       El objeto HttpServletRequest.
     * @param response      El objeto HttpServletResponse.
     * @param authException La excepción de autenticación.
     * @throws IOException      en caso de error de E/S.
     * @throws ServletException en caso de error en el servlet.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // Obtener la URL completa de la solicitud
        String urlRequest = SecurityUtils.getFullURL(request);
        // Obtener la IP del cliente
        String ip = SecurityUtils.getClientIP(request);

        // Manejar diferentes tipos de excepciones de autenticación
        if (authException instanceof BadCredentialsException
                || authException instanceof InternalAuthenticationServiceException) {
            // El token no es válido. Puede añadirse un control para bloquear la IP.
            System.err.println("Error de autenticación. IP: " + ip + ". Request URL: " + urlRequest);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Credenciales erróneas");
        } else {
            // No autorizado
            System.err.println("Error de petición. IP: " + ip + ". Request URL: " + urlRequest);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
        }
    }
}