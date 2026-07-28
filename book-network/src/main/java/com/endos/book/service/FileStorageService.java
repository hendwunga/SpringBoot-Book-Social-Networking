package com.endos.book.service;

// Import Java annotation dan Spring file upload
import jakarta.annotation.Nonnull;
import org.springframework.web.multipart.MultipartFile;

// Interface untuk layanan penyimpanan file — implementasi di FileStorageServiceImpl
public interface FileStorageService {

    // Simpan file ke server → return path file yang tersimpan
    // sourceFile: file yang diupload dari frontend
    // bookId: ID buku (untuk penamaan folder)
    // userId: ID user pemilik buku
    String saveFile(@Nonnull MultipartFile sourceFile,
                    @Nonnull Integer bookId,
                    @Nonnull Integer userId);
}
