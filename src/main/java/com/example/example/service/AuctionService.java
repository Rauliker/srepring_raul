package com.example.example.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import com.example.example.entity.Auction;
import com.example.example.entity.User;
import com.example.example.repository.AuctionRepository;
import com.example.example.repository.ItemRepository;
import com.example.example.repository.UserRepository;
import com.example.example.securty.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Validated
public class AuctionService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private JwtProvider JwtProvider;

    public List<Auction> findAll() {
        return auctionRepository.findAll();
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

    public Optional<Auction> findById(Long id) {
        return auctionRepository.findById(id);
    }

    public Auction save(Auction auction, HttpServletRequest request) {

        String username = this.obtenerUsuarioDesdeToken(request);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            logger.error("Usuario no encontrado con el correo electrónico o nombre de usuario: {}",
                    auction.getUser().getUsername());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Usuario no encontrado con el correo electrónico o nombre de usuario proporcionado");
        }

        auction.setUser(user.get());

        if (auction.getItem().getId() == null || !itemRepository.existsById(auction.getItem().getId())) {
            logger.error("Artículo no encontrado con id: {}", auction.getItem().getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado con el id proporcionado");
        }

        Optional<Auction> existingAuction = auctionRepository.findByItemAndEndTimeGreaterThan(auction.getItem(),
                LocalDateTime.now());
        if (existingAuction.isPresent()) {
            logger.error("El artículo {} ya está en una subasta activa", auction.getItem().getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El artículo ya está en una subasta activa");
        }

        auction.setItem(itemRepository.findById(auction.getItem().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado")));

        if (auction.getStartTime() != null && auction.getEndTime() != null) {
            if (auction.getStartTime().isAfter(auction.getEndTime())) {
                LocalDateTime temp = auction.getStartTime();
                auction.setStartTime(auction.getEndTime());
                auction.setEndTime(temp);
            }
        }

        logger.info("Guardando subasta: {}", auction.getId());
        return auctionRepository.save(auction);
    }

    public void deleteById(Long id, HttpServletRequest request) {
        String username = this.obtenerUsuarioDesdeToken(request);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            logger.error("Usuario no encontrado con el correo electrónico o nombre de usuario: {}", username);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Usuario no encontrado con el correo electrónico o nombre de usuario proporcionado");
        }

        List<Auction> auctions = auctionRepository.findByUserName(username);
        if (auctions.isEmpty()) {
            logger.error("No hay subastas para el usuario con id: {}", user.get().getEmail());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay subastas para el usuario");
        }

        // Verificar si la subasta con el ID proporcionado existe
        Optional<Auction> auctionToDelete = auctionRepository.findById(id);
        if (auctionToDelete.isEmpty()) {
            logger.error("La subasta con id {} no existe", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La subasta no existe");
        }

        // Verificar si la subasta pertenece al usuario autenticado
        if (!auctionToDelete.get().getUser().getUsername().equals(username)) {
            logger.error("El usuario {} no tiene permisos para eliminar la subasta con id {}", username, id);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para eliminar esta subasta");
        }

        auctionRepository.deleteById(id);
        logger.info("Subasta con id {} eliminada correctamente por el usuario {}", id, username);
    }

}
