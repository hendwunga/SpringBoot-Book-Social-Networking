package com.endos.book.entity;

// Import JPA, Lombok
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

// Entity feedback/review untuk buku — warisi BaseEntity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Feedback extends BaseEntity {

    private Double note;    // Rating (1-5), bisa desimal
    private String comment; // Komentar/review teks

    // Buku yang diberi feedback
    @ManyToOne
    @JoinColumn(name = "book_id") // Kolom FK ke tabel book
    private Book book;
}
