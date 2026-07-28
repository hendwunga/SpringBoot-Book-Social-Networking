package com.endos.book.repository;

// Import entity dan Spring Data JPA
import com.endos.book.entity.BookTransactionHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// Repository untuk entity BookTransactionHistory — menyediakan CRUD + query peminjaman
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    // Ambil semua buku yang dipinjam oleh user tertentu
    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :userId
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);

    // Ambil semua buku yang dipinjam orang lain, dimana user adalah pemiliknya
    // Dipakai di /books/returned untuk owner melihat siapa yang meminjam bukunya
    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :userId
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);

    // Cek apakah user tertentu sudah meminjam buku ini (dan belum return/approve)
    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.user.id = :userId
            AND bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser(@Param("bookId") Integer bookId, @Param("userId") Integer userId);

    // Cek apakah buku ini sudah dipinjam oleh siapapun (dan belum di-approve)
    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowed(@Param("bookId") Integer bookId);

    // Cari transaksi peminjaman berdasarkan bookId dan userId peminjam
    // Syarat: belum returned, belum approved
    @Query("""
            SELECT transaction
            FROM BookTransactionHistory  transaction
            WHERE transaction.user.id = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = false
            AND transaction.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);

    // Cari transaksi berdasarkan bookId dan ownerId (pemilik buku)
    // Syarat: sudah returned, tapi belum approved
    // Dipakai saat owner mau approve pengembalian
    @Query("""
            SELECT transaction
            FROM BookTransactionHistory  transaction
            WHERE transaction.book.owner.id = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = true
            AND transaction.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(@Param("bookId") Integer bookId, @Param("userId") Integer userId);
}
