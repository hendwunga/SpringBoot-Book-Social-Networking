import {Component, OnInit} from '@angular/core';
import {PageResponseBorrowedBookResponse} from '../../../../services/models/page-response-borrowed-book-response';
// Auto-generated service — panggil GET /api/v1/books/returned, PATCH /api/v1/books/borrow/return/approve/{id}
import {BookService} from '../../../../services/services/book.service';
import {BorrowedBookResponse} from '../../../../services/models/borrowed-book-response';

// Komponen "Return Books" — tampilkan buku yang dipinjam orang lain (owner only)
// Terhubung ke backend:
//   BookController.findAllReturnedBooks() → BookServiceImpl.findAllReturnedBooks()
//   BookController.approveReturnBorrowBook() → BookServiceImpl.approveReturnBorrowedBook()
@Component({
  selector: 'app-return-books',
  templateUrl: './return-books.component.html',
  styleUrls: ['./return-books.component.scss']
})
export class ReturnBooksComponent implements OnInit {

  page = 0;
    size = 5;
    pages: any = [];
    returnedBooks: PageResponseBorrowedBookResponse = {};
    message = '';    // Pesan sukses/gagal
    level: 'success' |'error' = 'success';

    constructor(
      private bookService: BookService // Generated: panggil backend
    ) {
    }

    ngOnInit(): void {
      this.findAllReturnedBooks();
    }

    private findAllReturnedBooks() {
      // Panggil GET /api/v1/books/returned?page=0&size=5
      this.bookService.findAllReturnedBooks({
        page: this.page,
        size: this.size
      }).subscribe({
        next: (resp) => {
          this.returnedBooks = resp;
          this.pages = Array(this.returnedBooks.totalPages)
            .fill(0)
            .map((x, i) => i);
        }
      });
    }

    // Navigasi halaman
    gotToPage(page: number) { this.page = page; this.findAllReturnedBooks(); }
    goToFirstPage() { this.page = 0; this.findAllReturnedBooks(); }
    goToPreviousPage() { this.page --; this.findAllReturnedBooks(); }
    goToLastPage() { this.page = this.returnedBooks.totalPages as number - 1; this.findAllReturnedBooks(); }
    goToNextPage() { this.page++; this.findAllReturnedBooks(); }
    get isLastPage() { return this.page === this.returnedBooks.totalPages as number - 1; }

    // Setujui pengembalian buku — panggil PATCH /api/v1/books/borrow/return/approve/{book-id}
    approveBookReturn(book: BorrowedBookResponse) {
      if (!book.returned) {
        return; // Buku belum dikembalikan peminjam → tidak bisa approve
      }
      this.bookService.approveReturnBorrowBook({
        'book-id': book.id as number
      }).subscribe({
        next: () => {
          this.level = 'success';
          this.message = 'Book return approved';
          this.findAllReturnedBooks(); // Refresh list
        }
      });
    }
  }
