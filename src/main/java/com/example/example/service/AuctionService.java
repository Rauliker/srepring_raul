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

    public List<Auction> findAll() {
        return auctionRepository.findAll();
    }

    public Optional<Auction> findById(Long id) {
        return auctionRepository.findById(id);
    }

    public Auction save(Auction auction) {
        Optional<User> user = userRepository.findByEmailOrUsername(auction.getUser().getEmail(),
                auction.getUser().getUsername());
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

    public void deleteById(Long id) {
        auctionRepository.deleteById(id);
    }

    public Optional<Auction> update(Long id, Auction auctionDetails) {
        Optional<Auction> existingAuction = auctionRepository.findById(id);
        if (existingAuction.isEmpty()) {
            logger.error("Subasta no encontrada con el id: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subasta no encontrada con el id proporcionado");
        }

        Auction existingAuctionEntity = existingAuction.get();

        // Asegurarse de que no se modifique el usuario, el artículo ni el highestBid
        if (!auctionDetails.getUser().equals(existingAuctionEntity.getUser())) {
            logger.error("No se puede cambiar el creador de la subasta");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cambiar el creador de la subasta");
        }

        if (!auctionDetails.getItem().equals(existingAuctionEntity.getItem())) {
            logger.error("No se puede cambiar el artículo de la subasta");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede cambiar el artículo de la subasta");
        }

        if (auctionDetails.getHighestBid() != null
                && !auctionDetails.getHighestBid().equals(existingAuctionEntity.getHighestBid())) {
            logger.error("No se puede cambiar la oferta más alta de la subasta");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cambiar la oferta más alta de la subasta");
        }

        // Si la fecha de inicio o finalización es válida, actualizar las fechas
        if (auctionDetails.getStartTime() != null && auctionDetails.getEndTime() != null) {
            if (auctionDetails.getStartTime().isAfter(auctionDetails.getEndTime())) {
                LocalDateTime temp = auctionDetails.getStartTime();
                auctionDetails.setStartTime(auctionDetails.getEndTime());
                auctionDetails.setEndTime(temp);
            }
        }

        logger.info("Actualizando subasta: {}", auctionDetails.getId());
        auctionDetails.setId(id);
        return Optional.of(auctionRepository.save(auctionDetails));
    }
}
