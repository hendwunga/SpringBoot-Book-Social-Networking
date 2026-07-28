import {Component, OnInit} from '@angular/core';
// Auto-generated service — panggil GET /api/v1/books/borrowed
import {BookService} from '../../../../services/services/book.service';
import {PageResponseBorrowedBookResponse} from '../../../../services/models/page-response-borrowed-book-response';
import {BorrowedBookResponse} from '../../../../services/models/borrowed-book-response';
import {BookResponse} from '../../../../services/models/book-response';
// Auto-generated model — request body { note, comment, bookId }
import {FeedbackRequest} from '../../../../services/models/feedback-request';
// Auto-generated service — panggil POST /api/v1/feedbacks
import {FeedbackService} from '../../../../services/services/feedback.service';

// Komponen "Borrowed Books" — tampilkan buku yang sedang dipinjam
// Terhubung ke backend:
//   BookController.findAllBorrowedBooks() → BookServiceImpl.findAllBorrowedBooks()
//   BookController.returnBorrowedBook() → BookServiceImpl.returnBorrowedBook()
//   FeedbackController.saveFeedback() → FeedbackServiceImpl.save()
@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrls: ['./borrowed-book-list.component.scss']
})
export class BorrowedBookListComponent implements OnInit {
  page = 0;
  size = 5;
  pages: any = [];
  borrowedBooks: PageResponseBorrowedBookResponse = {};
  selectedBook: BookResponse | undefined = undefined; // Buku yang dipilih untuk dikembalikan
  feedbackRequest: FeedbackRequest = {bookId: 0, comment: '', note: 0}; // Form feedback

  constructor(
    private bookService: BookService,     // Generated: panggil backend
    private feedbackService: FeedbackService // Generated: panggil backend
  ) {
  }

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }

  private findAllBorrowedBooks() {
    // Panggil GET /api/v1/books/borrowed?page=0&size=5
    this.bookService.findAllBorrowedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp) => {
        this.borrowedBooks = resp;
        this.pages = Array(this.borrowedBooks.totalPages)
          .fill(0)
          .map((x, i) => i);
      }
    });
  }

  // Navigasi halaman
  gotToPage(page: number) { this.page = page; this.findAllBorrowedBooks(); }
  goToFirstPage() { this.page = 0; this.findAllBorrowedBooks(); }
  goToPreviousPage() { this.page --; this.findAllBorrowedBooks(); }
  goToLastPage() { this.page = this.borrowedBooks.totalPages as number - 1; this.findAllBorrowedBooks(); }
  goToNextPage() { this.page++; this.findAllBorrowedBooks(); }
  get isLastPage() { return this.page === this.borrowedBooks.totalPages as number - 1; }

  // Pilih buku untuk dikembalikan
  returnBorrowedBook(book: BorrowedBookResponse) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number; // Set bookId untuk feedback
  }

  // Kembalikan buku — panggil PATCH /api/v1/books/borrow/return/{book-id}
  returnBook(withFeedback: boolean) {
    this.bookService.returnBorrowBook({
      'book-id': this.selectedBook?.id as number
    }).subscribe({
      next: () => {
        if (withFeedback) {
          this.giveFeedback(); // Berikan feedback setelah return
        }
        this.selectedBook = undefined; // Reset selection
        this.findAllBorrowedBooks();   // Refresh list
      }
    });
  }

  // Berikan feedback — panggil POST /api/v1/feedbacks
  private giveFeedback() {
    this.feedbackService.saveFeedback({
      body: this.feedbackRequest // { note, comment, bookId }
    }).subscribe({
      next: () => {
        // Feedback tersimpan
      }
    });
  }
}
