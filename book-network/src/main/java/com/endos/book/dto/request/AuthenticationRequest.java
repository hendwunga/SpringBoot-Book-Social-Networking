package com.endos.book.dto.request;

// Import validation annotations dan Lombok
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// DTO untuk request login — frontend mengirim email + password ke POST /auth/authenticate
@Getter
@Setter
public class AuthenticationRequest {

    @Email(message = "Email is not well formatted")   // Validasi format email
    @NotEmpty(message = "Email is mandatory")          // Tidak boleh string kosong
    @NotNull(message = "Email is mandatory")           // Tidak boleh null
    private String email;      // Email yang digunakan sebagai username

    @NotEmpty(message = "Password is mandatory")
    @NotNull(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum") // Minimal 8 karakter
    private String password;   // Password plain text (akan dicocokkan dengan hash di DB)
}
