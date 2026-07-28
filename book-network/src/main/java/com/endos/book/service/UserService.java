package com.endos.book.service;

// Import DTOs dan Spring Security
import com.endos.book.common.PageResponse;
import com.endos.book.dto.response.UserResponse;
import org.springframework.security.core.Authentication;

// Interface untuk layanan user — implementasi di UserServiceImpl
public interface UserService {

    // Ambil semua user (Admin only) — dengan pagination
    PageResponse<UserResponse> findAllUsers(int page, int size);

    // Cari user berdasarkan ID
    UserResponse findUserById(Integer userId);

    // Toggle status kunci akun user (lock/unlock)
    void toggleAccountLock(Integer userId);

    // Toggle status aktif/nonaktif akun user (enable/disable)
    void toggleAccountEnabled(Integer userId);

    // Ambil profil user yang sedang login
    UserResponse getOwnProfile(Authentication connectedUser);
}
