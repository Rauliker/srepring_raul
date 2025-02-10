package com.example.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.example.entity.Bid;

public interface BidRepository extends JpaRepository<Bid, Long> {
    @Query("SELECT b FROM Bid b WHERE b.auction.id = :auctionId ORDER BY b.amount DESC")
    List<Bid> findHighestBidsByAuctionId(@Param("auctionId") Long auctionId);

    @Query("SELECT b FROM Bid b WHERE b.user.id = :userId")
    List<Bid> findByUserId(@Param("userId") Long userId);
}
