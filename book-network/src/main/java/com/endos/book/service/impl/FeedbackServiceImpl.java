package com.endos.book.service.impl;

// Import dependency
import com.endos.book.common.PageResponse;
import com.endos.book.dto.request.FeedbackRequest;
import com.endos.book.dto.response.FeedbackResponse;
import com.endos.book.entity.Book;
import com.endos.book.entity.Feedback;
import com.endos.book.entity.User;
import com.endos.book.exception.OperationNotPermittedException;
import com.endos.book.repository.BookRepository;
import com.endos.book.repository.FeedbackRepository;
import com.endos.book.service.FeedbackMapper;
import com.endos.book.service.FeedbackService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

// Implementasi FeedbackService — menangani review/feedback buku
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final BookRepository bookRepository;       // Cari buku
    private final FeedbackMapper feedbackMapper;       // Konversi entity ↔ DTO
    private final FeedbackRepository feedBackRepository; // Simpan/cari feedback

    // ========== SIMPAN FEEDBACK ==========
    @Override
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        // 1. Cari buku di database
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));

        // 2. Validasi: buku harus aktif dan shareable
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
        }

        User user = ((User) connectedUser.getPrincipal());

        // 3. Validasi: tidak boleh feedback buku sendiri
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }

        // 4. Simpan feedback
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedBackRepository.save(feedback).getId();
    }

    // ========== AMBIL FEEDBACK PER BUKU ==========
    @Transactional
    @Override
    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable= PageRequest.of(page,size);
        User user=((User) connectedUser.getPrincipal()); // User yang sedang login

        // Query semua feedback untuk buku ini
        Page<Feedback> feedbacks=feedBackRepository.findAllByBookId(bookId,pageable);

        // Konversi ke DTO — sertakan info ownFeedback (apakah ini feedback saya?)
        List<FeedbackResponse> feedbackResponses=feedbacks.stream()
                .map(f-> feedbackMapper.toFeedbackResponse(f,user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
