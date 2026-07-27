package com.endos.book.controller;

import com.endos.book.common.PageResponse;
import com.endos.book.dto.response.UserResponse;
import com.endos.book.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all users - Admin only")
    public ResponseEntity<PageResponse<UserResponse>> findAllUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size) {
        return ResponseEntity.ok(userService.findAllUsers(page, size));
    }

    @GetMapping("/{user-id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get user by ID - Admin only")
    public ResponseEntity<UserResponse> findUserById(@PathVariable("user-id") Integer userId) {
        return ResponseEntity.ok(userService.findUserById(userId));
    }

    @PatchMapping("/{user-id}/lock")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Toggle user lock status - Admin only")
    public ResponseEntity<Void> toggleAccountLock(@PathVariable("user-id") Integer userId) {
        userService.toggleAccountLock(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{user-id}/enable")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Toggle user enabled status - Admin only")
    public ResponseEntity<Void> toggleAccountEnabled(@PathVariable("user-id") Integer userId) {
        userService.toggleAccountEnabled(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("profile")
    @Operation(summary = "Get own profile - Any authenticated user")
    public ResponseEntity<UserResponse> getOwnProfile(Authentication connectedUser) {
        return ResponseEntity.ok(userService.getOwnProfile(connectedUser));
    }
}
