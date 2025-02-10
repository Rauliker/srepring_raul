package com.example.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.example.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // User findByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    // @Query("SELECT u FROM User u WHERE u.role = :role")
    // List<User> findUsersByRole(@Param("role") String role);

    // @Query("SELECT u FROM User u WHERE u.userName LIKE %:userName%")
    // List<User> findUsersByUserNameContaining(@Param("userName") String userName);
}