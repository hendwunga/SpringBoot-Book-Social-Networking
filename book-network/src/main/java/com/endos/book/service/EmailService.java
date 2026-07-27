package com.endos.book.service;

import com.endos.book.common.EmailTemplateName;
import jakarta.mail.MessagingException;

public interface EmailService {

    void sendEmail(String to, String username, EmailTemplateName emailTemplate,
                   String confirmationUrl, String activationCode, String subject) throws MessagingException;
}
