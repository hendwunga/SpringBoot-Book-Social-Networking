package com.endos.book.entity;

// Import Jackson, JPA, Lombok, Spring Data
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

// Entity role — tidak warisi BaseEntity karena punya struktur sendiri
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)      // Nama role harus unik (USER, ADMIN)
    private String name;         // "USER" atau "ADMIN"

    // Relasi Many-to-Many inverse: satu role dimiliki banyak user
    // @JsonIgnore mencegah infinite recursion saat serialisasi JSON
    @ManyToMany(mappedBy="roles") // "roles" adalah field di entity User
    @JsonIgnore
    private Set<User> users;

    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
