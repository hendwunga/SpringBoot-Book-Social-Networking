package com.endos.book.service;

// Import DTO, email template, dan Java Mail
import com.endos.book.common.EmailTemplateName;
import jakarta.mail.MessagingException;

// Interface untuk layanan email — implementasi di EmailServiceImpl
public interface EmailService {

    // Kirim email dengan template HTML Thymeleaf
    // to: alamat email penerima
    // username: nama lengkap penerima (ditampilkan di template)
    // emailTemplate: nama template (ACTIVATE_ACCOUNT)
    // confirmationUrl: URL aktifasi dari frontend
    // activationCode: kode OTP 6 digit
    // subject: subjek email
    void sendEmail(String to, String username, EmailTemplateName emailTemplate,
                   String confirmationUrl, String activationCode, String subject) throws MessagingException;
}
