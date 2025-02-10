package com.example.example.securty;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Proveedor de JWT que maneja la generación y validación de tokens JWT.
 */
@Component
public class JwtProvider {

    // Clave secreta para firmar el token JWT
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private int expiration;

    /**
     * Genera un token JWT basado en la autenticación.
     *
     * @param authentication La autenticación del usuario.
     * @return El token JWT generado.
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token El token JWT.
     * @return El nombre de usuario extraído del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param token El token JWT.
     * @return La fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un reclamo específico del token JWT.
     *
     * @param token          El token JWT.
     * @param claimsResolver La función para resolver el reclamo.
     * @param <T>            El tipo del reclamo.
     * @return El reclamo extraído.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los reclamos del token JWT.
     *
     * @param token El token JWT.
     * @return Los reclamos extraídos del token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Verifica si el token JWT ha expirado.
     *
     * @param token El token JWT.
     * @return true si el token ha expirado, false en caso contrario.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida el token JWT.
     *
     * @param token El token JWT.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean validateToken(String token) {
        System.out.println("token: " + secret);
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("token mal formado");
        } catch (UnsupportedJwtException e) {
            System.err.println("token no soportado");
        } catch (ExpiredJwtException e) {
            System.err.println("token expirado");
        } catch (IllegalArgumentException e) {
            System.err.println("token vacío");
        }
        return false;
    }

    /**
     * Valida el token JWT contra los detalles del usuario.
     *
     * @param token       El token JWT.
     * @param userDetails Los detalles del usuario.
     * @return true si el token es válido y coincide con los detalles del usuario,
     *         false en caso contrario.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}