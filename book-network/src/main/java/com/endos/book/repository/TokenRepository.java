package com.endos.book.repository;

// Import entity dan Spring Data JPA
import com.endos.book.entity.Token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// Repository untuk entity Token — menyimpan dan mencari JWT tokens
public interface TokenRepository extends JpaRepository<Token, Integer> {

    // Cari semua token yang masih valid (tidak expired + tidak revoked) untuk user tertentu
    // Dipakai saat login baru: revoke semua token lama user tersebut
    @Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false and t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Integer id);

    // Cari token berdasarkan access token (JWT string)
    // Dipakai di JwtFilter untuk validasi token
    Optional<Token> findByToken(String token);

    // Cari token berdasarkan refresh token (UUID string)
    // Dipakai di refresh token endpoint
    Optional<Token> findByRefreshToken(String token);
}
