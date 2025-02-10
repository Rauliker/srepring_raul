package com.example.example.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.example.entity.Auction;
import com.example.example.entity.Item;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Query("SELECT a FROM Auction a WHERE a.user.id = :userId")
    List<Auction> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Auction a WHERE a.user.email = :userEmail")
    List<Auction> findByUserEmail(@Param("userEmail") String userEmail);

    @Query("SELECT a FROM Auction a WHERE a.user.username = :userName")
    List<Auction> findByUserName(@Param("userName") String userName);

    @Query("SELECT a FROM Auction a WHERE a.item.id = :itemId")
    List<Auction> findByItemId(@Param("itemId") Long itemId);

    @Query("SELECT a FROM Auction a WHERE a.item = :item AND a.endTime > :currentDateTime")
    Optional<Auction> findByItemAndEndTimeGreaterThan(@Param("item") Item item,
            @Param("currentDateTime") LocalDateTime currentDateTime);
}
