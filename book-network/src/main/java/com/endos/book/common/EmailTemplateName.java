package com.endos.book.common;

// Import Lombok
import lombok.Getter;

// Enum untuk nama template email — dipakai di EmailService
@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"); // Template: activate_account.html di resources/templates/

    private final String name; // Nama file template tanpa ekstensi

    EmailTemplateName(String name) {
        this.name = name;
    }
}
