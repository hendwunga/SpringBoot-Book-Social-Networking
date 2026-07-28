package com.endos.book.repository;

// Import entity dan Spring Data JPA
import com.endos.book.entity.Feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Repository untuk entity Feedback — menyediakan CRUD + query feedback per buku
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    // Ambil semua feedback untuk buku tertentu, dengan pagination
    @Query("""
            SELECT feedback
            FROM Feedback  feedback
            WHERE feedback.book.id = :bookId
            """)
    Page<Feedback> findAllByBookId(@Param("bookId") Integer bookId, Pageable pageable);
}
