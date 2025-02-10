package com.example.example.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.example.dto.JwtDto;
import com.example.example.dto.LoginUsuario;
import com.example.example.dto.RespuestaDto;
import com.example.example.entity.User;
import com.example.example.securty.JwtProvider;
import com.example.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador de autenticación que maneja las solicitudes de inicio de sesión y
 * generación de JWT.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Inyección de dependencias para manejar la autenticación
    @Autowired
    private AuthenticationManager authenticationManager;

    // Inyección de dependencias para manejar la generación de JWT
    @Autowired
    private JwtProvider jwtProvider;

    // Inyección de dependencias para manejar los servicios de usuario
    @Autowired
    private UserService userService;

    /**
     * Endpoint para el login.
     *
     * @param loginUsuario Objeto que contiene el nombre de usuario y la contraseña.
     * @param request      Objeto HttpServletRequest para obtener información de la
     *                     solicitud.
     * @return ResponseEntity con el JWT y los detalles del usuario, o un mensaje de
     *         error.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUsuario loginUsuario, HttpServletRequest request) {

        // Obtener la IP del cliente y la URL de la solicitud
        String loginUsername = loginUsuario.getNombreUsuario();
        // Buscar el usuario por nombre de usuario
        Optional<User> optUser = userService.findByUsername(loginUsername);
        System.out.println("hola " + optUser);
        // Si el usuario no existe, devolver un error
        if (optUser.isEmpty()) {
            return new ResponseEntity<>(new RespuestaDto("Usuario o password incorrectos."), HttpStatus.BAD_REQUEST);
        }
        if (!optUser.get().getPassword().equals(loginUsuario.getPassword())) {
            return new ResponseEntity<>(new RespuestaDto("Usuario o password incorrectos."), HttpStatus.BAD_REQUEST);

        }
        User user = optUser.get();

        String jwt = jwtProvider.generateToken(user.getUsername());

        // Creamos el DTO del JWT. Como no usamos roles, se envía una lista vacía o
        // null.
        JwtDto jwtDto = new JwtDto(jwt, user.getUsername(), new ArrayList<>());

        // Devolver la respuesta con el JWT
        return ResponseEntity.ok(jwtDto);
    }
}