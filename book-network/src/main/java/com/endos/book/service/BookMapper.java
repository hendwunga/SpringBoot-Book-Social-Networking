package com.endos.book.service;

// Import DTOs, entity, dan FileUtils
import com.endos.book.common.FileUtils;
import com.endos.book.dto.request.BookRequest;
import com.endos.book.dto.response.BookResponse;
import com.endos.book.dto.response.BorrowedBookResponse;
import com.endos.book.entity.Book;
import com.endos.book.entity.BookTransactionHistory;
import org.springframework.stereotype.Service;

// Mapper: konversi antara entity Book ↔ DTO BookRequest/BookResponse
// Dipisah dari service untuk menjaga tanggung jawab (Single Responsibility)
@Service
public class BookMapper {

    // Konversi BookRequest (dari frontend) → entity Book (untuk disimpan ke DB)
    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())               // Null saat create, terisi saat update
                .title(request.title())          // Judul buku
                .isbn(request.isbn())            // ISBN
                .authorName(request.authorName())// Nama penulis
                .synopsis(request.synopsis())    // Sinopsis
                .archived(false)                 // Default: tidak diarsipkan
                .shareable(request.shareable())  // Status shareable dari request
                .build();
    }

    // Konversi entity Book → BookResponse (untuk dikirim ke frontend)
    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())            // Rating rata-rata (dihitung transient)
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName()) // Nama pemilik: "Hendro Wunga"
                .cover(FileUtils.readFileFromLocation(book.getBookCover())) // Baca file cover → byte[]
                .build();
    }

    // Konversi BookTransactionHistory → BorrowedBookResponse (untuk halaman borrowed/returned)
    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())         // ID buku
                .title(history.getBook().getTitle())   // Judul buku
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())        // Sudah dikembalikan?
                .returnApproved(history.isReturnApproved()) // Sudah disetujui?
                .build();
    }
}
