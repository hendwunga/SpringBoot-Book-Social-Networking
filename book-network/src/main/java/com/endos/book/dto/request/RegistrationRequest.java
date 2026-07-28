package com.endos.book.dto.request;

// Import validation annotations dan Lombok
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

// DTO untuk request registrasi — frontend mengirim data ke POST /auth/register
@Getter
@Setter
@Builder   // Supaya bisa pakai RegistrationRequest.builder().firstname("Hendro")...
public class RegistrationRequest {

    @NotEmpty(message = "Firstname is mandatory")
    @NotNull(message = "Firstname is mandatory")
    private String firstname;   // Nama depan

    @NotEmpty(message = "Lastname is mandatory")
    @NotNull(message = "Lastname is mandatory")
    private String lastname;    // Nama belakang

    @Email(message = "Email is not well formatted")
    @NotEmpty(message = "Email is mandatory")
    @NotNull(message = "Email is mandatory")
    private String email;       // Email (akan jadi username + login)

    @NotEmpty(message = "Password is mandatory")
    @NotNull(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    private String password;    // Password (akan di-hash sebelum disimpan ke DB)
}
