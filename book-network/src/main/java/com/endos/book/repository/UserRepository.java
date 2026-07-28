package com.endos.book.repository;

// Import entity dan Spring Data JPA
import com.endos.book.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository untuk entity User — menyediakan CRUD + query by email
public interface UserRepository extends JpaRepository<User,Integer> {

    // Cari user berdasarkan email — dipakai saat login dan loadUserByUsername
    Optional<User> findByEmail(String username);
}
