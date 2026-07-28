import {Component, OnInit} from '@angular/core';
import {PageResponseBookResponse} from '../../../../services/models/page-response-book-response';
// Auto-generated service — panggil GET /api/v1/books/owner
import {BookService} from '../../../../services/services/book.service';
import {BookResponse} from '../../../../services/models/book-response';
import {Router} from '@angular/router';

// Komponen "My Books" — tampilkan buku milik user yang sedang login
// Terhubung ke backend: BookController.findAllBooksByOwner() → BookServiceImpl.findAllBooksByOwner()
@Component({
  selector: 'app-my-books',
  templateUrl: './my-books.component.html',
  styleUrls: ['./my-books.component.scss']
})
export class MyBooksComponent implements OnInit {

  bookResponse: PageResponseBookResponse = {};
  page = 0;
  size = 5;
  pages: any = [];

  constructor(
    private bookService: BookService, // Generated: panggil backend
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.findAllBooks();
  }

  private findAllBooks() {
    // Panggil GET /api/v1/books/owner?page=0&size=5
    this.bookService.findAllBooksByOwner({
      page: this.page,
      size: this.size
    })
      .subscribe({
        next: (books) => {
          this.bookResponse = books;
          this.pages = Array(this.bookResponse.totalPages)
            .fill(0)
            .map((x, i) => i);
        }
      });
  }

  // Navigasi halaman
  gotToPage(page: number) { this.page = page; this.findAllBooks(); }
  goToFirstPage() { this.page = 0; this.findAllBooks(); }
  goToPreviousPage() { this.page --; this.findAllBooks(); }
  goToLastPage() { this.page = this.bookResponse.totalPages as number - 1; this.findAllBooks(); }
  goToNextPage() { this.page++; this.findAllBooks(); }
  get isLastPage() { return this.page === this.bookResponse.totalPages as number - 1; }

  // Toggle status archived — panggil PATCH /api/v1/books/archived/{book-id}
  archiveBook(book: BookResponse) {
    this.bookService.updateArchivedStatus({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        book.archived = !book.archived; // Toggle status di UI
      }
    });
  }

  // Toggle status shareable — panggil PATCH /api/v1/books/shareable/{book-id}
  shareBook(book: BookResponse) {
    this.bookService.updateShareableStatus({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        book.shareable = !book.shareable; // Toggle status di UI
      }
    });
  }

  // Buka halaman edit buku
  editBook(book: BookResponse) {
    this.router.navigate(['books', 'manage', book.id]);
  }
}
