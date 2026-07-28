import {Component, OnInit} from '@angular/core';
import {BookResponse} from '../../../../services/models/book-response';
// Auto-generated service — panggil GET /api/v1/books/{id}, GET /api/v1/feedbacks/book/{id}
import {BookService} from '../../../../services/services/book.service';
import {ActivatedRoute} from '@angular/router';
import {FeedbackService} from '../../../../services/services/feedback.service';
import {PageResponseFeedbackResponse} from '../../../../services/models/page-response-feedback-response';

// Komponen detail buku — tampilkan info buku + daftar feedback
// Terhubung ke backend:
//   BookController.findBookById() → BookServiceImpl.findById()
//   FeedbackController.findAllFeedbackByBook() → FeedbackServiceImpl.findAllFeedbackByBook()
@Component({
  selector: 'app-book-details',
  templateUrl: './book-details.component.html',
  styleUrls: ['./book-details.component.scss']
})
export class BookDetailsComponent implements OnInit {
  book: BookResponse = {};              // Data buku dari backend
  feedbacks: PageResponseFeedbackResponse = {}; // Feedback list dari backend
  page = 0;
  size = 5;
  pages: any = [];
  private bookId = 0; // ID buku dari URL

  constructor(
    private bookService: BookService,       // Generated: panggil backend
    private feedbackService: FeedbackService, // Generated: panggil backend
    private activatedRoute: ActivatedRoute    // Ambil parameter dari URL
  ) {}

  ngOnInit(): void {
    // Ambil bookId dari URL: /books/details/{bookId}
    this.bookId = this.activatedRoute.snapshot.params['bookId'];
    if (this.bookId) {
      // Panggil GET /api/v1/books/{book-id}
      this.bookService.findBookById({
        'book-id': this.bookId
      }).subscribe({
        next: (book: BookResponse) => {
          this.book = book; // Simpan data buku
          this.findAllFeedbacks(); // Load feedback setelah buku berhasil di-load
        }
      });
    }
  }

  private findAllFeedbacks() {
    // Panggil GET /api/v1/feedbacks/book/{book-id}?page=0&size=5
    this.feedbackService.findAllFeedbackByBook({
      'book-id': this.bookId,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (data: PageResponseFeedbackResponse) => {
        this.feedbacks = data; // Simpan feedback list
      }
    });
  }

  // Navigasi halaman feedback
  gotToPage(page: number) { this.page = page; this.findAllFeedbacks(); }
  goToFirstPage() { this.page = 0; this.findAllFeedbacks(); }
  goToPreviousPage() { this.page--; this.findAllFeedbacks(); }
  goToLastPage() { this.page = this.feedbacks.totalPages as number - 1; this.findAllFeedbacks(); }
  goToNextPage() { this.page++; this.findAllFeedbacks(); }
  get isLastPage() { return this.page === (this.feedbacks.totalPages as number) - 1; }
}
