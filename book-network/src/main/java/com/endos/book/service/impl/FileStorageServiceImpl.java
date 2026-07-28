package com.endos.book.service.impl;

// Import dependency
import com.endos.book.service.FileStorageService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;  // "/" di Linux, "\" di Windows
import static java.lang.System.currentTimeMillis; // Timestamp unik untuk nama file

// Implementasi FileStorageService — simpan file cover buku ke filesystem
@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    // Path folder output — dari application.yml (contoh: "uploads/photos")
    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;

    // Simpan file cover buku → return path file yang tersimpan
    @Override
    public String saveFile(
            @Nonnull MultipartFile sourceFile,  // File dari frontend
            @Nonnull Integer bookId,            // ID buku
            @Nonnull Integer userId             // ID user pemilik
    ) {
        // Buat subfolder: users/{userId}/
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    // Upload file ke folder yang sudah ditentukan
    private String uploadFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String fileUploadSubPath
    ) {
        // Gabungkan path: uploads/photos/users/{userId}/
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        // Buat folder jika belum ada
        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs(); // Recursive mkdir
            if (!folderCreated) {
                log.warn("Failed to create the target folder: " + targetFolder);
                return null;
            }
        }

        // Ambil ekstensi file (jpg, png, dll)
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());

        // Buat nama file unik: {timestamp}.{ext}
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);

        try {
            // Tulis bytes file ke disk
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to: " + targetFilePath);
            return targetFilePath; // Return path lengkap
        } catch (IOException e) {
            log.error("File was not saved", e);
        }
        return null; // Gagal menyimpan
    }

    // Ambil ekstensi file dari nama file (contoh: "photo.jpg" → "jpg")
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return ""; // Tidak ada ekstensi
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
