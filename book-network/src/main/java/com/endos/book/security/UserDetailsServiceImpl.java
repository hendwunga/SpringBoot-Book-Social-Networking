package com.endos.book.security;

// Import Spring Security dan repository
import com.endos.book.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Implementasi UserDetailsService — load user dari database untuk Spring Security
// Dipanggil oleh JwtFilter dan AuthenticationManager saat login
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    // Load user berdasarkan email — dipanggil saat login dan validasi JWT
    @Override
    @Transactional  // Agar lazy-loaded roles bisa diakses
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException{
        return repository.findByEmail(userEmail)        // Cari user di database
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
