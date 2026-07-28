package com.endos.book.service;

// Import DTOs dan Spring Security
import com.endos.book.common.PageResponse;
import com.endos.book.dto.request.FeedbackRequest;
import com.endos.book.dto.response.FeedbackResponse;
import org.springframework.security.core.Authentication;

// Interface untuk layanan feedback/review — implementasi di FeedbackServiceImpl
public interface FeedbackService {

    // Simpan feedback baru untuk buku → return feedback ID
    Integer save(FeedbackRequest request, Authentication connectedUser);

    // Ambil semua feedback untuk buku tertentu, dengan info apakah milik user sendiri
    PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser);
}
