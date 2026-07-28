package com.endos.book.dto.request;

// Import validation annotations
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// DTO untuk request buku — menggunakan Java Record (immutable, otomatis punya constructor + getter)
// Digunakan di POST /books dan PATCH /books
public record BookRequest(
        Integer id,             // Null saat create, terisi saat update
        @NotNull(message = "100")   // Error code 100: title wajib
        @NotEmpty(message = "100")
        String title,           // Judul buku
        @NotNull(message = "101")   // Error code 101: authorName wajib
        @NotEmpty(message = "101")
        String authorName,      // Nama penulis
        @NotNull(message = "102")   // Error code 102: isbn wajib
        @NotEmpty(message = "102")
        String isbn,            // ISBN buku
        @NotNull(message = "103")   // Error code 103: synopsis wajib
        @NotEmpty(message = "103")
        String synopsis,        // Sinopsis/ringkasan buku
        boolean shareable) {    // Apakah buku bisa dipinjam orang lain
}
