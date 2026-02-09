package com.neuroisp.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingEmailService {

    private final JavaMailSender mailSender;

    public void sendPdf(
            String to,
            String from,
            String subject,
            String body,
            byte[] pdf,
            String filename
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.addAttachment(filename, new ByteArrayResource(pdf));

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send billing email", e);
        }
    }
}
