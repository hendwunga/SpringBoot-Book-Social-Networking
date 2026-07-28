package com.endos.book.dto.response;

// Import Lombok
import lombok.*;

// DTO response untuk feedback/review buku — dikirim ke frontend di GET /feedbacks/book/{id}
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

    private Double note;       // Rating (1-5)
    private String comment;    // Teks review
    private boolean ownFeedback; // True jika feedback ini milik user yang sedang login
}
