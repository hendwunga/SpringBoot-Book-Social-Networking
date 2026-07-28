package com.endos.book.service.impl;

// Import dependency
import com.endos.book.common.EmailTemplateName;
import com.endos.book.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async; // Kirim email di thread terpisah (tidak block request)
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

// Implementasi EmailService — kirim email dengan template HTML Thymeleaf
@Service
@Slf4j       // Auto-generate logger (log.info, log.error, dll)
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;          // Sender email (dari Spring Mail)
    private final SpringTemplateEngine templateEngine; // Template engine Thymeleaf

    @Async   // Dijalankan di thread terpisah — tidak menunggu email terkirim
    @Override
    public void sendEmail(String to, String username, EmailTemplateName emailTemplate, String confirmationUrl, String activationCode, String subject ) throws MessagingException {
        // 1. Tentukan nama template (default: "confirm-email")
        String templateName;
        if (emailTemplate == null) {
            templateName = "confirm-email";
        } else {
            templateName = emailTemplate.getName(); // "activate_account"
        }

        // 2. Buat MIME message (support HTML + gambar)
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,    // Mode campuran (HTML + lampiran)
                UTF_8.name()             // Encoding UTF-8
        );

        // 3. Isi variabel template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);         // Nama penerima (ditampilkan di email)
        properties.put("confirmationUrl", confirmationUrl); // URL aktifasi
        properties.put("activation_code", activationCode);  // Kode OTP 6 digit

        // 4. Proses template Thymeleaf → HTML string
        Context context = new Context();
        context.setVariables(properties);

        // 5. Set pengirim, penerima, subjek
        helper.setFrom("contact@hendrowunga.com");  // Alamat pengirim
        helper.setTo(to);                           // Alamat penerima
        helper.setSubject(subject);                 // Subjek email

        // 6. Render template → HTML
        String template = templateEngine.process(templateName, context);

        // 7. Kirim email (HTML mode)
        helper.setText(template, true); // true = HTML, bukan plain text
        mailSender.send(mimeMessage);
    }
}
