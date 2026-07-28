package com.endos.book.common;

// Import entity dan JPA Specification
import com.endos.book.entity.Book;

import org.springframework.data.jpa.domain.Specification;

// Utility class untuk membuat JPA Specification — dynamic query untuk Book
public class BookSpecification {

    // Specification untuk filter buku berdasarkan owner ID
    // Dipakai di BookServiceImpl.findAllBooksByOwner() untuk My Books
    // Query: WHERE book.owner.id = :ownerId
    public static Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
