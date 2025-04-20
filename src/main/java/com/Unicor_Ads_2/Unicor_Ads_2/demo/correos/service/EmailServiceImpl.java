package com.Unicor_Ads_2.Unicor_Ads_2.demo.correos.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    final private JavaMailSender mailSender;

    @Value("${email.sender}")
    private String emailUser;

    @Override
    public void sendEmail(String[] toUser, String subject, String message) {
        try {
            // PRIMERO creas el MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            // Luego creas el helper que trabaja sobre el mimeMessage
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Configuras el correo
            helper.setFrom(emailUser);
            helper.setTo(toUser);
            helper.setSubject(subject);
            helper.setText(message, true); // true porque queremos enviar HTML

            // Envías
            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }

    @Override
    public void sendEmailWithFile(String []toUser, String subject, String message, File file) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            mimeMessageHelper.setFrom(emailUser);
            mimeMessageHelper.setTo(toUser);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(file.getName(), file);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
