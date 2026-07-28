package com.endos.book.config;

// Import Spring Data Auditing dan Security
import com.endos.book.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// Implementasi AuditorAware — beritahu Spring Data siapa yang membuat/mengubah record
// Digunakan oleh @CreatedBy dan @LastModifiedBy di BaseEntity
public class ApplicationAuditAware implements AuditorAware<Integer> {
    @Override
    public Optional<Integer> getCurrentAuditor() {
        // Ambil authentication dari SecurityContext (user yang sedang login)
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        // Jika tidak ada user atau user anonymous → return kosong
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken){
            return Optional.empty();
        }

        // Ambil ID user dari principal (User entity)
        User userPrincipal=(User) authentication.getPrincipal();

        return Optional.ofNullable(userPrincipal.getId()); // Return user ID
    }
}
