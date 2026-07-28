package com.endos.book.service.impl;

// Import semua dependency
import com.endos.book.common.BookSpecification;
import com.endos.book.common.PageResponse;
import com.endos.book.dto.request.BookRequest;
import com.endos.book.dto.response.BookResponse;
import com.endos.book.dto.response.BorrowedBookResponse;
import com.endos.book.entity.Book;
import com.endos.book.entity.BookTransactionHistory;
import com.endos.book.entity.User;
import com.endos.book.exception.OperationNotPermittedException;
import com.endos.book.repository.BookRepository;
import com.endos.book.repository.BookTransactionHistoryRepository;
import com.endos.book.service.BookService;
import com.endos.book.service.BookMapper;
import com.endos.book.service.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.endos.book.common.BookSpecification.withOwnerId;

// Implementasi BookService — menangani semua operasi buku
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;                    // Akses tabel buku
    private final BookTransactionHistoryRepository transactionHistoryRepository; // Akses tabel peminjaman
    private final BookMapper bookMapper;                            // Konversi entity ↔ DTO
    private final FileStorageService fileStorageService;             // Simpan file cover

    // ========== SIMPAN BUKU ==========
    @Override
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal()); // Ambil user dari token JWT
        Book book = bookMapper.toBook(request);            // Konversi DTO → entity
        book.setOwner(user);                               // Set pemilik buku = user login
        return bookRepository.save(book).getId();          // Simpan ke DB → return ID
    }

    // ========== CARI BUKU BY ID ==========
    @Override
    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)           // Konversi entity → DTO
                .orElseThrow(()-> new EntityNotFoundException("No book found with the ID :: " + bookId ));
    }

    // ========== BROWSING BUKU (untuk user biasa) ==========
    @Override
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // Pagination: sort by createdDate descending (terbaru dulu)
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdDate").descending());
        // Query: buku non-archived + shareable + bukan milik sendiri
        Page<Book> books=bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponse=books.stream()
                .map(bookMapper::toBookResponse) // Konversi setiap entity ke DTO
                .toList();
        return new PageResponse<>(          // Bungkus dalam PageResponse
                bookResponse,
                books.getNumber(),          // Halaman saat ini
                books.getSize(),            // Size per halaman
                books.getTotalElements(),   // Total semua buku
                books.getTotalPages(),      // Total halaman
                books.isFirst(),            // Apakah halaman pertama
                books.isLast()              // Apakah halaman terakhir
        );
    }

    // ========== MY BOOKS (buku milik user sendiri) ==========
    @Override
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdDate").descending());
        // Pakai Specification: filter by owner.id
        Page<Book> books=bookRepository.findAll(withOwnerId(user.getId()),pageable);

        List<BookResponse> bookResponse=books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    // ========== BUKU YANG DIPINJAM ==========
    @Override
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        // Query: semua transaksi peminjaman milik user ini
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    // ========== BUKU YANG DIPINJAM ORANG LAIN (returned books untuk owner) ==========
    @Override
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        // Query: transaksi yang book.owner = user ini
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    // ========== TOGGLE SHAREABLE ==========
    @Override
    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        // Hanya pemilik buku yang boleh update
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable()); // Toggle: true→false atau false→true
        bookRepository.save(book);
        return bookId;
    }

    // ========== TOGGLE ARCHIVED ==========
    @Override
    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        // Hanya pemilik buku yang boleh update
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }
        book.setArchived(!book.isArchived()); // Toggle archive
        bookRepository.save(book);
        return bookId;
    }

    // ========== PINJAM BUKU ==========
    @Override
    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

        // Validasi: buku harus aktif dan shareable
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());

        // Validasi: tidak boleh pinjam buku sendiri
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }

        // Validasi: user ini belum meminjam buku ini
        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }

        // Validasi: buku belum dipinjam orang lain
        final boolean isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(bookId);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("Te requested book is already borrowed");
        }

        // Buat transaksi peminjaman baru
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)                // Peminjam
                .book(book)                // Buku yang dipinjam
                .returned(false)           // Belum dikembalikan
                .returnApproved(false)     // Belum disetujui
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    // ========== KEMBALIKAN BUKU ==========
    @Override
    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());
        // Tidak boleh kembalikan buku sendiri
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }

        // Cari transaksi peminjaman user ini untuk buku ini
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true); // Tandai sudah dikembalikan
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    // ========== APPROVE PENGEMBALIAN ==========
    @Override
    public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book is archived or not shareable");
        }

        User user = ((User) connectedUser.getPrincipal());
        // Hanya pemilik buku yang boleh approve
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you do not own");
        }

        // Cari transaksi yang sudah returned=true, returnApproved=false
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));

        bookTransactionHistory.setReturnApproved(true); // Setujui pengembalian
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }


    // ========== UPLOAD COVER BUKU ==========
    @Override
    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        // Simpan file ke server → return path file
        var bookCover = fileStorageService.saveFile(file, bookId, user.getId());
        book.setBookCover(bookCover); // Simpan path ke field bookCover
        bookRepository.save(book);
    }
}
