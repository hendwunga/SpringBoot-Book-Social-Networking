package com.endos.book.dto.response;

// Import Lombok
import lombok.*;

// DTO response untuk data buku — dikumpan ke frontend di GET /books
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    private Integer id;            // ID buku di database
    private String title;          // Judul buku
    private String authorName;     // Nama penulis
    private String isbn;           // ISBN buku
    private String synopsis;       // Sinopsis buku
    private String owner;          // Nama lengkap pemilik buku (firstname + lastname)
    private byte[] cover;          // Gambar cover dalam bentuk byte[] (dibaca dari file)
    private double rate;           // Rata-rata rating dari semua feedback
    private boolean archived;      // Status arsip buku
    private boolean shareable;     // Status shareable buku
}
