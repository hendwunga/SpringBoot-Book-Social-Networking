package com.endos.book.entity;

// Import JPA, Lombok, dan Spring Data Auditing
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Base class untuk semua entity — menyimpan id, timestamps, dan siapa yang membuat/mengubah
@Getter
@Setter
@SuperBuilder                // SuperBuilder agar child class bisa pakai builder pattern
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass            // Tabel ini tidak dibuat sendiri, tapi diwarisi oleh child entity
@EntityListeners(AuditingEntityListener.class) // Auto-isi createdDate/modifiedDate pakai Spring Data JPA Auditing
public class BaseEntity {

    @Id
    @GeneratedValue          // Auto-increment primary key
    private Integer id;

    @CreatedDate             // Auto-isi waktu saat pertama kali record dibuat
    @Column(nullable = false,updatable = false) // Tidak boleh null dan tidak bisa diupdate
    private LocalDateTime createdDate;

    @LastModifiedDate        // Auto-isi waktu saat terakhir kali record diupdate
    @Column(insertable = false) // Tidak diisi saat insert, hanya saat update
    private LocalDateTime lastModifiedDate;

    @CreatedBy               // Auto-isi ID user yang membuat record
    @Column(nullable = false,updatable = false)
    private Integer createdBy;

    @LastModifiedBy          // Auto-isi ID user yang terakhir mengubah record
    @Column(insertable = false)
    private Integer lastModifiedBy;
}
