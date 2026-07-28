package com.endos.book.entity;

// Import JPA, Lombok
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

// Entity riwayat peminjaman buku — warisi BaseEntity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class BookTransactionHistory extends BaseEntity {

    // User yang meminjam buku ini
    @ManyToOne
    @JoinColumn(name = "user_id")  // Kolom FK ke tabel user
    private User user;

    // Buku yang dipinjam
    @ManyToOne
    @JoinColumn(name = "book_id")  // Kolom FK ke tabel book
    private Book book;

    private boolean returned;       // True = buku sudah dikembalikan oleh peminjam
    private boolean returnApproved; // True = pengembalian sudah disetujui oleh pemilik buku
}
