package com.example.example.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            logger.info("Enviando correo a: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            logger.info("Correo enviado a: {}", to);
        } catch (MailAuthenticationException e) {
            logger.error("Error de autenticación: {}", e.getMessage());
        } catch (MailSendException e) {
            logger.error("Error al enviar el correo: {}", e.getMessage());
        } catch (MailException e) {
            logger.error("Error general de correo: {}", e.getMessage());
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            logger.info("Enviando correo con adjunto a: {}", to);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Adjunto", file);

            mailSender.send(message);
            logger.info("Correo con adjunto enviado a: {}", to);
        } catch (MessagingException | MailException e) {
            logger.error("Error al enviar el correo con adjunto: {}", e.getMessage());
        }
    }
}