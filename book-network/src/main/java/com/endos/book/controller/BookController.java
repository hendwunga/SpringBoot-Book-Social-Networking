package com.endos.book.controller;

// Import DTOs, service, dan annotations
import com.endos.book.common.PageResponse;
import com.endos.book.dto.request.BookRequest;
import com.endos.book.dto.response.BookResponse;
import com.endos.book.dto.response.BorrowedBookResponse;
import com.endos.book.service.BookService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// REST Controller untuk operasi buku (CRUD, borrow, return, cover)
@RestController
@RequestMapping("books")          // Base path: /api/v1/books/*
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService service;

    // POST /books — simpan buku baru atau update
    @PostMapping
    public ResponseEntity<Integer> saveBook(@Valid @RequestBody BookRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(service.save(request, connectedUser)); // Return book ID
    }

    // GET /books/{book-id} — cari buku by ID (public)
    @GetMapping("/{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId) {
        return ResponseEntity.ok(service.findById(bookId));
    }

    // GET /books?page=0&size=10 — browsing buku (hanya buku shareable, bukan milik sendiri)
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllBooks(page, size, connectedUser));
    }

    // GET /books/owner?page=0&size=10 — buku milik user sendiri
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllBooksByOwner(page, size, connectedUser));
    }

    // GET /books/borrowed?page=0&size=10 — buku yang sedang dipinjam
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllBorrowedBooks(page, size, connectedUser));
    }

    // GET /books/returned?page=0&size=10 — buku milik saya yang dipinjam orang lain
    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.findAllReturnedBooks(page, size, connectedUser));
    }

    // PATCH /books/shareable/{book-id} — toggle status shareable
    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.updateShareableStatus(bookId, connectedUser));
    }

    // PATCH /books/archived/{book-id} — toggle status archived
    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.updateArchivedStatus(bookId, connectedUser));
    }

    // POST /books/borrow/{book-id} — pinjam buku
    @PostMapping("borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.borrowBook(bookId, connectedUser));
    }

    // PATCH /books/borrow/return/{book-id} — kembalikan buku
    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable("book-id") Integer bookId, Authentication connectedUser) {
        return ResponseEntity.ok(service.returnBorrowedBook(bookId, connectedUser));
    }

    // PATCH /books/borrow/return/approve/{book-id} — approve pengembalian (owner only)
    @PatchMapping("borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser) {
        return ResponseEntity.ok(service.approveReturnBorrowedBook(bookId, connectedUser));
    }

    // POST /books/cover/{book-id} — upload cover buku (multipart/form-data)
    @PostMapping(value = "/cover/{book-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter() @RequestPart("file") MultipartFile file, // File gambar dari frontend
            Authentication connectedUser) {
        service.uploadBookCoverPicture(file, connectedUser, bookId);
        return ResponseEntity.accepted().build(); // 202 Accepted
    }
}
