package com.example.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.example.entity.Bid;
import com.example.example.service.BidService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bids")
public class BidController {
    @Autowired
    private BidService bidService;

    @GetMapping
    public List<Bid> getAllBids() {
        return bidService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bid> getBidById(@PathVariable Long id) {
        return bidService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Bid> createBid(@Valid @RequestBody Bid bid) {
        return ResponseEntity.ok(bidService.save(bid));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bid> updateBid(@PathVariable Long id, @Valid @RequestBody Bid bidDetails) {
        return bidService.update(id, bidDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBid(@PathVariable Long id) {
        bidService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
