package com.endos.book.service.impl;

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

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedBackRepository;


    @Override
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback for and archived or not shareable book");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedBackRepository.save(feedback).getId();
    }

    @Transactional
    @Override
    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable= PageRequest.of(page,size);
        User user=((User) connectedUser.getPrincipal());
        Page<Feedback> feedbacks=feedBackRepository.findAllByBookId(bookId,pageable);
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
