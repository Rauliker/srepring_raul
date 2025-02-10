package com.example.example.securty;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtils {

    /**
     * Método para obtener la URL completa de la solicitud.
     *
     * @param request El objeto HttpServletRequest.
     * @return La URL completa de la solicitud.
     */
    public static String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestURL.append("?").append(queryString);
        }
        return requestURL.toString();
    }

    /**
     * Método para obtener la dirección IP del cliente.
     *
     * @param request El objeto HttpServletRequest.
     * @return La dirección IP del cliente.
     */
    public static String getClientIP(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            remoteAddr = xForwardedFor.split(",")[0];
        }
        return remoteAddr;
    }
}
