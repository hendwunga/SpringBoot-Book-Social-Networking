package com.endos.book.controller;

// Import DTOs, service, dan annotations
import com.endos.book.common.PageResponse;
import com.endos.book.dto.response.UserResponse;
import com.endos.book.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Cek role sebelum eksekusi
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// REST Controller untuk manajemen user (Admin only untuk semua endpoint kecuali profile)
@RestController
@RequestMapping("users")          // Base path: /api/v1/users/*
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    // GET /users?page=0&size=10 — ambil semua user (Admin only)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Hanya role ADMIN yang boleh akses
    @Operation(summary = "Get all users - Admin only")
    public ResponseEntity<PageResponse<UserResponse>> findAllUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        return ResponseEntity.ok(userService.findAllUsers(page, size));
    }

    // GET /users/{user-id} — cari user by ID (Admin only)
    @GetMapping("/{user-id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get user by ID - Admin only")
    public ResponseEntity<UserResponse> findUserById(@PathVariable("user-id") Integer userId) {
        return ResponseEntity.ok(userService.findUserById(userId));
    }

    // PATCH /users/{user-id}/lock — toggle lock akun user (Admin only)
    @PatchMapping("/{user-id}/lock")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Toggle user lock status - Admin only")
    public ResponseEntity<Void> toggleAccountLock(@PathVariable("user-id") Integer userId) {
        userService.toggleAccountLock(userId);
        return ResponseEntity.ok().build();
    }

    // PATCH /users/{user-id}/enable — toggle enable/disable akun user (Admin only)
    @PatchMapping("/{user-id}/enable")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Toggle user enabled status - Admin only")
    public ResponseEntity<Void> toggleAccountEnabled(@PathVariable("user-id") Integer userId) {
        userService.toggleAccountEnabled(userId);
        return ResponseEntity.ok().build();
    }

    // GET /users/profile — ambil profil sendiri (any authenticated user)
    @GetMapping("profile")
    @Operation(summary = "Get own profile - Any authenticated user")
    public ResponseEntity<UserResponse> getOwnProfile(Authentication connectedUser) {
        return ResponseEntity.ok(userService.getOwnProfile(connectedUser));
    }
}
