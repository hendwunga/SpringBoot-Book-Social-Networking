package com.endos.book.service;

import com.endos.book.common.PageResponse;
import com.endos.book.dto.request.FeedbackRequest;
import com.endos.book.dto.response.FeedbackResponse;
import org.springframework.security.core.Authentication;

public interface FeedbackService {

    Integer save(FeedbackRequest request, Authentication connectedUser);

    PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser);
}
