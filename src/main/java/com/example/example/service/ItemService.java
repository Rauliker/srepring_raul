package com.example.example.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.example.entity.Item;
import com.example.example.repository.ItemRepository;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        logger.info("Saving item: {}", item.getId());
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado");
        }
        itemRepository.deleteById(id);
    }

    public Optional<Item> update(Long id, Item itemDetails) {
        Optional<Item> itemOptional = itemRepository.findById(id);

        if (itemOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item no encontrado");
        }

        Item item = itemOptional.get();

        if (itemDetails.getName() == null || itemDetails.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del item no puede estar vacío");
        }

        if (itemDetails.getStartingPrice() == null || itemDetails.getStartingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio inicial debe ser un valor positivo");
        }

        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());
        item.setStartingPrice(itemDetails.getStartingPrice());

        logger.info("Updating item: {}", id);
        return Optional.of(itemRepository.save(item));
    }
}
