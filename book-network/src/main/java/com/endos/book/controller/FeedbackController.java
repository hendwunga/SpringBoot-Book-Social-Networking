package com.endos.book.controller;

// Import DTOs, service, dan annotations
import com.endos.book.common.PageResponse;
import com.endos.book.dto.request.FeedbackRequest;
import com.endos.book.dto.response.FeedbackResponse;
import com.endos.book.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// REST Controller untuk feedback/review buku
@RestController
@RequestMapping("feedbacks")      // Base path: /api/v1/feedbacks/*
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService service;

    // POST /feedbacks — simpan feedback baru
    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest request, // note + comment + bookId
            Authentication connectedUser                 // Dari token JWT
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser)); // Return feedback ID
    }

    // GET /feedbacks/book/{book-id}?page=0&size=10 — ambil semua feedback untuk buku tertentu
    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam (name="page",defaultValue="0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser // Untuk cek ownFeedback
    ){
        return ResponseEntity.ok(service.findAllFeedbackByBook(bookId,page,size,connectedUser));
    }
}
