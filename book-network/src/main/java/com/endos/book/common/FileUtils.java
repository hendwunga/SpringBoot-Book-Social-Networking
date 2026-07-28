package com.endos.book.common;

// Import utility classes
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Utility untuk membaca file dari filesystem → byte[]
// Dipakai oleh BookMapper untuk membaca cover buku
@Slf4j
public class FileUtils {

    // Baca file dari path → return byte[] (gambar cover)
    // fileUrl: path lengkap file (contoh: "uploads/photos/users/1/1234567890.jpg")
    public static byte[] readFileFromLocation(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return null; // Path kosong → tidak ada cover
        }
        try {
            Path filePath = new File(fileUrl).toPath(); // Konversi String → Path
            return Files.readAllBytes(filePath);        // Baca semua bytes file
        } catch (IOException e) {
            log.warn("Nou file found in the path {}", fileUrl); // File tidak ditemukan
        }
        return null;
    }
}
