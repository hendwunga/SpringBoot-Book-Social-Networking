package com.endos.book.config;

// Import Swagger/OpenAPI annotations
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

// Konfigurasi Swagger/OpenAPI — otomatis generate docs di /swagger-ui.html
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Hendro",
                        email = "hendrowunga073@gmail.com",
                        url = "https://github.com/hendrowunga/Book-Social-Networking"
                ),
                description = "This is the OpenAPI documentation for the Book Social Networking application...",
                title = "Book Social Networking API Documentation",
                version = "1.0",
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local Development Environment",
                        url = "http://localhost:8088/api/v1"
                ),
                @Server(
                        description = "Production Environment",
                        url = "https://api.booknetworking.com/v1"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth" // Semua endpoint butuh JWT
                )
        }
)
// Skema autentikasi: Bearer JWT di header Authorization
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication scheme for securing API endpoints.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // Konfigurasi murni annotations — tidak perlu method
}
