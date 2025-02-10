package com.example.example.securty;

import java.io.IOException; // Correcta importación de IOException

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por solicitud.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Método que filtra cada solicitud para autenticar el usuario basado en el
     * token JWT.
     *
     * @param request     El objeto HttpServletRequest.
     * @param response    El objeto HttpServletResponse.
     * @param filterChain La cadena de filtros.
     * @throws ServletException en caso de error en el servlet.
     * @throws IOException      en caso de error de E/S.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Obtener el token JWT de la solicitud
        String jwt = getJwtFromRequest(request);

        // Validar el token JWT
        if (jwt != null && jwtProvider.validateToken(jwt)) {
            // Obtener el nombre de usuario del token JWT
            String username = jwtProvider.extractUsername(jwt);

            // Cargar los detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Crear la autenticación basada en el token JWT
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Obtiene el token JWT de la solicitud.
     *
     * @param request El objeto HttpServletRequest.
     * @return El token JWT si está presente, de lo contrario null.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}