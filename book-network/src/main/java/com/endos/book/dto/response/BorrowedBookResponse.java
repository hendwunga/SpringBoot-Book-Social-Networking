package com.endos.book.dto.response;

// Import Lombok
import lombok.*;

// DTO response untuk buku yang dipinjam — dikirim ke frontend di GET /books/borrowed dan /books/returned
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowedBookResponse {

    private Integer id;            // ID buku
    private String title;          // Judul buku
    private String authorName;     // Nama penulis
    private String isbn;           // ISBN buku
    private double rate;           // Rating rata-rata buku
    private boolean returned;      // Sudah dikembalikan oleh peminjam?
    private boolean returnApproved;// Pengembalian sudah disetujui pemilik?
}
