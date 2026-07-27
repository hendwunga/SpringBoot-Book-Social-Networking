package com.endos.book.service;

import jakarta.annotation.Nonnull;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String saveFile(@Nonnull MultipartFile sourceFile,
                    @Nonnull Integer bookId,
                    @Nonnull Integer userId);
}
