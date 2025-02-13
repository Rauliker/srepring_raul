package com.example.example.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.example.entity.User;
import com.example.example.repository.UserRepository;
import com.example.example.securty.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider JwtProvider;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String userneme) {
        return userRepository.findByUsername(userneme);
    }

    public String obtenerUsuarioDesdeToken(HttpServletRequest request) {
        String token = extraerTokenDeRequest(request);
        if (token != null) {
            return JwtProvider.extractUsername(token);
        }
        return null;
    }

    private String extraerTokenDeRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public User save(User user) {
        // Validación de usuario único por nombre y correo
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede repetir el nombre de usuario");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede repetir el email");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
        }

        logger.info("Saving user: {}", user.getId());
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        // Eliminar el usuario
        userRepository.deleteById(id);
    }

    public Optional<User> update(Long id, User userDetails, HttpServletRequest request) {
        // Buscar el usuario por ID

        String username = this.obtenerUsuarioDesdeToken(request);
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        User user = userOptional.get();
        if (!user.getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para actualizar este usuario");
        }
        if (userRepository.existsByUsername(userDetails.getUsername())
                && !user.getUsername().equals(userDetails.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede repetir el nombre de usuario");
        }

        if (userRepository.existsByEmail(userDetails.getEmail())
                && !user.getEmail().equals(userDetails.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede repetir el email");
        }

        // Validar si la contraseña es válida
        if (userDetails.getPassword() != null && userDetails.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 6 caracteres");
        }

        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null) {
            user.setPassword(userDetails.getPassword());
        }

        logger.info("Updating user: {}", user.getId());
        return Optional.of(userRepository.save(user));
    }
}
