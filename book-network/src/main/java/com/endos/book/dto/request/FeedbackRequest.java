package com.endos.book.dto.request;

// Import validation annotations
import jakarta.validation.constraints.*;

// DTO untuk request feedback/review buku — digunakan di POST /feedbacks
public record FeedbackRequest(

        @Positive(message = "200")          // Error 200: note harus positif
        @Min(value = 0, message = "201")    // Error 201: minimum 0
        @Max(value = 5, message = "202")    // Error 202: maksimum 5
        Double note,                        // Rating 1-5

        @NotNull(message = "203")           // Error 203: comment wajib
        @NotEmpty(message = "203")
        @NotBlank(message = "203")
        String comment,                     // Teks review/komentar

        @NotNull(message = "204")           // Error 204: bookId wajib
        Integer bookId                      // ID buku yang diberi review
) {
}
