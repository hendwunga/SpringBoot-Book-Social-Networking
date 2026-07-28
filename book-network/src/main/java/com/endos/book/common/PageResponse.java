package com.endos.book.common;

// Import Lombok
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// DTO generic untuk response paginasi — dipakai di semua endpoint yang return list
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {
    private List<T> content;     // Daftar item di halaman ini
    private int number;          // Nomor halaman (0-based)
    private int size;            // Jumlah item per halaman
    private long totalElements;  // Total semua item di database
    private int totalPages;      // Total jumlah halaman
    private boolean first;       // Apakah ini halaman pertama?
    private boolean last;        // Apakah ini halaman terakhir?
}
