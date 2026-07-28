import {Component, OnInit} from '@angular/core';
import {PageResponseBookResponse} from '../../../../services/models/page-response-book-response';
// Auto-generated service — panggil GET /api/v1/books
import {BookService} from '../../../../services/services/book.service';
import {BookResponse} from '../../../../services/models/book-response';
import {Router} from '@angular/router';

// Komponen untuk browsing buku (halaman utama setelah login sebagai USER)
// Terhubung ke backend: BookController.findAllBooks() → BookServiceImpl.findAllBooks()
@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {
  bookResponse: PageResponseBookResponse = {}; // Response paginasi dari backend
  page = 0;    // Halaman saat ini (0-based)
  size = 5;    // Item per halaman
  pages: any = []; // Array nomor halaman untuk navigasi
  message = '';    // Pesan sukses/gagal
  level: 'success' |'error' = 'success'; // Tipe pesan

  constructor(
    private bookService: BookService, // Generated: panggil backend
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.findAllBooks(); // Load buku saat komponen diinisialisasi
  }

  private findAllBooks() {
    // Panggil GET /api/v1/books?page=0&size=5
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size
    })
      .subscribe({
        next: (books) => {
          this.bookResponse = books; // Simpan response
          this.pages = Array(this.bookResponse.totalPages)
            .fill(0)
            .map((x, i) => i); // Generate array [0, 1, 2, ...] untuk navigasi halaman
        }
      });
  }

  // Navigasi halaman
  gotToPage(page: number) {
    this.page = page;
    this.findAllBooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBooks();
  }

  goToPreviousPage() {
    this.page --;
    this.findAllBooks();
  }

  goToLastPage() {
    this.page = this.bookResponse.totalPages as number - 1;
    this.findAllBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllBooks();
  }

  get isLastPage() {
    return this.page === this.bookResponse.totalPages as number - 1;
  }

  // Pinjam buku — panggil POST /api/v1/books/borrow/{book-id}
  borrowBook(book: BookResponse) {
    this.message = '';
    this.level = 'success';
    this.bookService.borrowBook({
      'book-id': book.id as number // ID buku
    }).subscribe({
      next: () => {
        this.level = 'success';
        this.message = 'Book successfully added to your list';
      },
      error: (err) => {
        console.log(err);
        this.level = 'error';
        this.message = err.error.error; // Pesan error dari GlobalExceptionHandler
      }
    });
  }

  // Buka halaman detail buku
  displayBookDetails(book: BookResponse) {
    this.router.navigate(['books', 'details', book.id]);
  }
}
