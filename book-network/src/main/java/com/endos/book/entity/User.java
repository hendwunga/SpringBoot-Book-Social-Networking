package com.endos.book.entity;

// Import JPA, Lombok, Spring Security
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

// Lombok: auto-generate getter, setter, builder, constructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity                         // Tabel di database
@Table(name="_user")            // Nama tabel "_user" (karena "user" reserved di PostgreSQL)
@EntityListeners(AuditingEntityListener.class) // Auto-isi timestamps

// User implements UserDetails (Spring Security) dan Principal (untuk getName() di email template)
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue             // Auto-increment ID
    private Integer id;
    private String firstname;   // Nama depan user
    private String lastname;    // Nama belakang user
    private LocalDate dateOfBirth; // Tanggal lahir

    @Column(unique = true)      // Email harus unik di database
    private String email;       // Digunakan sebagai username untuk login
    private String password;    // Password yang sudah di-hash dengan BCrypt
    private boolean accountLocked; // True = akun dikunci (oleh admin), tidak bisa login
    private boolean enabled;     // True = akun aktif, harus aktifasi email dulu

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    // Relasi many-to-many dengan Role (USER, ADMIN) — diambil saat login (EAGER)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",                    // Tabel join untuk relasi user-role
            joinColumns = @JoinColumn(name = "user_id"),       // Kolom FK ke user
            inverseJoinColumns = @JoinColumn(name = "role_id")) // Kolom FK ke role
    private Set<Role> roles;

    // Principal.getName() — dipakai di email template Thymeleaf
    @Override
    public String getName() {
        return email;
    }

    // Spring Security: konversi Set<Role> menjadi Collection<GrantedAuthority>
    // Contoh: Role("USER") → SimpleGrantedAuthority("USER")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    // Spring Security pakai email sebagai username
    @Override
    public String getUsername() {
        return email;
    }

    // Akun selalu dianggap tidak expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Cek apakah akun tidak dikunci — kebalikan dari accountLocked
    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    // Kredensial (password) selalu dianggap tidak expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Akun harus enabled (sudah aktifasi email) agar bisa login
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Helper method: return "firstname lastname"
    public String fullName() {
        return getFirstname() + " " + getLastname();
    }

    // Sama dengan fullName() — digunakan di AuthServiceImpl untuk claims JWT
    public String getFullName() {
        return firstname + " " + lastname;
    }
}
