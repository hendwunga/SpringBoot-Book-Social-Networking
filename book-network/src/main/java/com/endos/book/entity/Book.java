package com.endos.book.entity;

// Import JPA, Lombok
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.util.List;

// Entity buku — warisi BaseEntity (id, timestamps, auditing)
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String title;          // Judul buku
    private String authorName;     // Nama penulis
    private String isbn;           // ISBN unik buku
    private String synopsis;       // Sinopsis/ringkasan buku
    private String bookCover;      // Path file gambar cover di server
    private boolean archived;      // True = buku diarsipkan (tidak tampil di browsing)
    private boolean shareable;     // True = buku bisa dipinjam orang lain

    // Relasi Many-to-One: banyak buku dimiliki oleh satu user (owner)
    @ManyToOne
    @JoinColumn(name = "owner_id") // Kolom FK di tabel book
    private User owner;

    // Relasi One-to-Many: satu buku punya banyak feedback/review
    @OneToMany(mappedBy = "book")  // "book" adalah field di entity Feedback
    private List<Feedback> feedbacks;

    // Relasi One-to-Many: satu buku punya banyak riwayat peminjaman
    @OneToMany(mappedBy = "book")  // "book" adalah field di entity BookTransactionHistory
    private List<BookTransactionHistory> histories;

    // Transient: tidak disimpan di database, dihitung saat runtime
    // Menghitung rata-rata rating dari semua feedback
    @Transient
    public double getRate(){
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0; // Belum ada review → rating 0
        }
        // Hitung rata-rata note (rating 1-5) dari semua feedback
        var rate= this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        // Bulatkan 1 desimal (contoh: 4.56 → 4.6)
        double roundedRate=Math.round(rate*10.0)/10.0;
        return roundedRate;
    }
}
