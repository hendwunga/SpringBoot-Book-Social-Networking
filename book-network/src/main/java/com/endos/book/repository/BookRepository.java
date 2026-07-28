package com.endos.book.repository;

// Import entity dan Spring Data JPA
import com.endos.book.entity.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

// Repository untuk entity Book — menyediakan CRUD + query custom
// JpaSpecificationExecutor: mendukung dynamic query pakai Specification (di BookSpecification)
public interface BookRepository extends JpaRepository<Book, Integer> , JpaSpecificationExecutor<Book> {

    // Query untuk browsing buku: hanya tampilkan buku yang:
    // 1. Tidak diarsipkan (archived = false)
    // 2. Bisa dipinjam (shareable = true)
    // 3. Bukan buku milik user yang sedang login (owner.id != userId)
    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.shareable = true
            AND book.owner.id != :userId
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);
}
