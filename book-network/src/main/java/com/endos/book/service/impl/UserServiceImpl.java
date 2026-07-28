package com.endos.book.service.impl;

// Import dependency
import com.endos.book.common.PageResponse;
import com.endos.book.dto.response.UserResponse;
import com.endos.book.entity.User;
import com.endos.book.repository.UserRepository;
import com.endos.book.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

// Implementasi UserService — menangani operasi user management (Admin)
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    // ========== AMBIL SEMUA USER (Admin only) ==========
    @Override
    public PageResponse<UserResponse> findAllUsers(int page, int size) {
        // Sort by createdDate descending (user terbaru dulu)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> users = userRepository.findAll(pageable);

        // Konversi setiap User entity → UserResponse DTO
        List<UserResponse> userResponse = users.stream()
                .map(this::toUserResponse)
                .toList();

        return new PageResponse<>(
                userResponse,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }

    // ========== CARI USER BY ID ==========
    @Override
    public UserResponse findUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return toUserResponse(user);
    }

    // ========== TOGGLE LOCK AKUN ==========
    @Override
    public void toggleAccountLock(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setAccountLocked(!user.isAccountLocked()); // Toggle: lock↔unlock
        userRepository.save(user);
    }

    // ========== TOGGLE ENABLED AKUN ==========
    @Override
    public void toggleAccountEnabled(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setEnabled(!user.isEnabled()); // Toggle: enable↔disable
        userRepository.save(user);
    }

    // ========== AMBIL PROFIL SENDIRI ==========
    @Override
    public UserResponse getOwnProfile(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal(); // Ambil user dari token JWT
        return toUserResponse(user);
    }

    // Helper: konversi User entity → UserResponse DTO
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .accountLocked(user.isAccountLocked())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName()) // Set<Role> → List<String>
                        .toList())
                .createdDate(user.getCreatedDate())
                .build();
    }
}
