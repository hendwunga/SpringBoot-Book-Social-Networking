import {Component, OnInit} from '@angular/core';
// Auto-generated model — request body { title, authorName, isbn, synopsis, shareable }
import {BookRequest} from '../../../../services/models/book-request';
// Auto-generated service — panggil POST /api/v1/books, POST /api/v1/books/cover/{id}
import {BookService} from '../../../../services/services/book.service';
import {ActivatedRoute, Router} from '@angular/router';

// Komponen untuk buat/edit buku + upload cover
// Terhubung ke backend:
//   BookController.saveBook() → BookServiceImpl.save() (create/update)
//   BookController.uploadBookCoverPicture() → BookServiceImpl.uploadBookCoverPicture()
//   BookController.findBookById() → BookServiceImpl.findById() (load data untuk edit)
@Component({
  selector: 'app-manage-book',
  templateUrl: './manage-book.component.html',
  styleUrls: ['./manage-book.component.scss']
})
export class ManageBookComponent implements OnInit {

  errorMsg: Array<string> = []; // Error validasi dari backend
  bookRequest: BookRequest = {
    authorName: '',
    isbn: '',
    synopsis: '',
    title: ''
  };
  selectedBookCover: any;          // File gambar cover yang dipilih
  selectedPicture: string | undefined; // Preview gambar (base64)

  constructor(
    private bookService: BookService, // Generated: panggil backend
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    const bookId = this.activatedRoute.snapshot.params['bookId'];
    if (bookId) {
      // Mode EDIT: load data buku dari backend
      // Panggil GET /api/v1/books/{book-id}
      this.bookService.findBookById({
        'book-id': bookId
      }).subscribe({
        next: (book) => {
          // Isi form dengan data buku yang sudah ada
         this.bookRequest = {
           id: book.id,
           title: book.title as string,
           authorName: book.authorName as string,
           isbn: book.isbn as string,
           synopsis: book.synopsis as string,
           shareable: book.shareable
         };
         // Tampilkan preview cover (base64 dari byte[])
         this.selectedPicture='data:image/jpg;base64,' + book.cover;
        }
      });
    }
  }

  saveBook() {
    // Panggil POST /api/v1/books (create atau update jika bookRequest.id ada)
    this.bookService.saveBook({
      body: this.bookRequest // { title, authorName, isbn, synopsis, shareable }
    }).subscribe({
      next: (bookId) => {
        // Buku tersimpan → upload cover
        // Panggil POST /api/v1/books/cover/{book-id} (multipart/form-data)
        this.bookService.uploadBookCoverPicture({
          'book-id': bookId,
          body: {
            file: this.selectedBookCover // File gambar dari input[type=file]
          }
        }).subscribe({
          next: () => {
            // Semua berhasil → redirect ke My Books
            this.router.navigate(['/books/my-books']);
          }
        });
      },
      error: (err) => {
        console.log(err.error);
        this.errorMsg = err.error.validationError; // Error validasi dari GlobalExceptionHandler
      }
    });
  }

  // Handler saat user memilih file gambar
  onFileSelected(event: any) {
    this.selectedBookCover = event.target.files[0]; // Ambil file pertama
    console.log(this.selectedBookCover);

    if (this.selectedBookCover) {
      // Buat preview gambar (base64)
      const reader = new FileReader();
      reader.onload = () => {
        this.selectedPicture = reader.result as string;
      };
      reader.readAsDataURL(this.selectedBookCover);
    }
  }
}
