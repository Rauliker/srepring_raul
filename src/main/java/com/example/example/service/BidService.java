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
import com.example.example.entity.Bid;
import com.example.example.entity.User;
import com.example.example.repository.AuctionRepository;
import com.example.example.repository.BidRepository;
import com.example.example.repository.UserRepository;

@Service
@Validated
public class BidService {
    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Bid> findAll() {
        return bidRepository.findAll();
    }

    public Optional<Bid> findById(Long id) {
        return bidRepository.findById(id);
    }

    public Bid save(Bid bid) {
        Optional<Auction> auctionOptional = auctionRepository.findById(bid.getAuction().getId());
        if (auctionOptional.isEmpty()) {
            logger.error("Subasta no encontrada con el id: {}", bid.getAuction().getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subasta no encontrada con el id proporcionado");
        }

        Auction auction = auctionOptional.get();

        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            logger.error("La subasta ha finalizado: {}", auction.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La subasta ha finalizado");
        }

        boolean result = bid.getAmount().compareTo(auction.getHighestBid()) <= 0;
        if (result) {
            logger.error("La puja no es mayor que la puja más alta actual: {}", auction.getHighestBid());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La puja debe ser mayor que la puja más alta actual");
        }
        Optional<User> userOptional = userRepository.findByEmailOrUsername(bid.getUser().getEmail(),
                bid.getUser().getUsername());
        if (userOptional.isEmpty()) {
            logger.error("Usuario no encontrado con el correo electrónico o nombre de usuario: {}",
                    bid.getUser().getUsername());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Usuario no encontrado con el correo electrónico o nombre de usuario proporcionado");
        }

        User user = userOptional.get();
        boolean creatorResult = auction.getUser().getId().equals(user.getId());
        if (creatorResult) {
            logger.error("El usuario que realiza la puja es el creador de la subasta: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El creador de la subasta no puede realizar una puja");
        }

        bid.setUser(user);

        logger.info("Guardando puja: {}", bid.getId());
        Bid createBid = bidRepository.save(bid);
        if (createBid != null) {
            auction.setHighestBid(bid.getAmount());
            auctionRepository.save(auction);
        }
        return createBid;
    }

    public void deleteById(Long id) {
        bidRepository.deleteById(id);
    }
}