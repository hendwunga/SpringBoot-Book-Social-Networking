package com.endos.book.service;

// Import DTOs dan entity
import com.endos.book.dto.request.FeedbackRequest;
import com.endos.book.dto.response.FeedbackResponse;
import com.endos.book.entity.Book;
import com.endos.book.entity.Feedback;
import org.springframework.stereotype.Service;

import java.util.Objects;

// Mapper: konversi antara entity Feedback ↔ DTO FeedbackRequest/FeedbackResponse
@Service
public class FeedbackMapper {

    // Konversi FeedbackRequest (dari frontend) → entity Feedback
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())          // Rating
                .comment(request.comment())    // Teks review
                .book(Book.builder()
                        .id(request.bookId())  // Hanya butuh ID buku (bukan full object)
                        .archived(false)
                        .shareable(false)
                        .build())
                .build();
    }

    // Konversi entity Feedback → FeedbackResponse
    // id: ID user yang sedang login — dipakai untuk cek ownFeedback
    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())       // Rating
                .comment(feedback.getComment()) // Teks review
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id)) // True jika ini feedback saya
                .build();
    }
}
