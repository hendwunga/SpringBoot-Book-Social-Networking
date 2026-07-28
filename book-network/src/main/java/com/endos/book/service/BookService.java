package com.endos.book.service;

// Import DTOs, Spring Security
import com.endos.book.common.PageResponse;
import com.endos.book.dto.request.BookRequest;
import com.endos.book.dto.response.BookResponse;
import com.endos.book.dto.response.BorrowedBookResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

// Interface untuk layanan buku — implementasi di BookServiceImpl
public interface BookService {

    // Simpan buku baru atau update buku exist → return book ID
    Integer save(BookRequest request, Authentication connectedUser);

    // Cari buku berdasarkan ID → return BookResponse
    BookResponse findById(Integer bookId);

    // Ambil semua buku yang bisa dibrowsing (bukan milik user, shareable, tidak archived)
    PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser);

    // Ambil semua buku milik user yang sedang login (untuk halaman "My Books")
    PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser);

    // Ambil semua buku yang dipinjam oleh user yang sedang login
    PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser);

    // Ambil semua buku yang dipinjam orang lain (milik user yang sedang login)
    PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser);

    // Toggle status shareable buku (hanya pemilik)
    Integer updateShareableStatus(Integer bookId, Authentication connectedUser);

    // Toggle status archived buku (hanya pemilik)
    Integer updateArchivedStatus(Integer bookId, Authentication connectedUser);

    // Pinjam buku orang lain — buat transaksi peminjaman
    Integer borrowBook(Integer bookId, Authentication connectedUser);

    // Kembalikan buku yang dipinjam — set returned=true
    Integer returnBorrowedBook(Integer bookId, Authentication connectedUser);

    // Setujui pengembalian buku — set returnApproved=true (hanya pemilik)
    Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser);

    // Upload gambar cover buku — simpan file ke server
    void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId);
}
