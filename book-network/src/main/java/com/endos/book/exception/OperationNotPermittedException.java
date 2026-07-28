package com.endos.book.exception;

// Custom exception untuk operasi yang tidak diizinkan
// Contoh: pinjam buku sendiri, feedback buku sendiri, update buku orang lain
public class OperationNotPermittedException extends RuntimeException {

    public OperationNotPermittedException(String msg) {
        super(msg); // Pesan error akan ditampilkan di frontend
    }
}
