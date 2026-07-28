package com.endos.book.repository;

// Import entity dan Spring Data JPA
import com.endos.book.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository untuk entity Role — menyediakan CRUD + query by name
public interface RoleRepository extends JpaRepository<Role,Integer> {

    // Cari role berdasarkan nama (contoh: "USER", "ADMIN")
    // Dipakai saat registrasi untuk assign role USER ke user baru
    Optional<Role> findByName(String role);
}
