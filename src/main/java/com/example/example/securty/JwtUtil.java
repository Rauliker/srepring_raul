package com.example.example.securty;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
    // Clave secreta para firmar el token (en producción usa algo más robusto y
    // configúralo externamente)
    private static final String SECRET_KEY = "mi_clave_secreta";

    // Genera un token para un usuario dado
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Token válido por 10 horas
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extrae el nombre de usuario (subject) del token
    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Comprueba si el token es válido para el usuario
    public static boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    // Obtiene las “claims” (información) del token
    private static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // Verifica si el token ha expirado
    private static boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
