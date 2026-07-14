package com.vrms.application;

import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailNotificationService implements NotificationService {

    private final String username;
    private final String password;

    public EmailNotificationService() {
        Dotenv dotenv = Dotenv.load();

        this.username = dotenv.get("EMAIL_USERNAME");
        this.password = dotenv.get("EMAIL_PASSWORD");

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalStateException("EMAIL_USERNAME is missing from .env");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalStateException("EMAIL_PASSWORD is missing from .env");
        }
    }

    @Override
    public void sendNotification(String recipientEmail, String subject, String message) {
    	Properties properties = new Properties();

    	properties.put("mail.smtp.auth", "true");
    	properties.put("mail.smtp.starttls.enable", "true");
    	properties.put("mail.smtp.starttls.required", "true");
    	properties.put("mail.smtp.host", "smtp.gmail.com");
    	properties.put("mail.smtp.port", "587");
    	properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.replace(" ", ""));
            }
        });

        try {
            Message email = new MimeMessage(session);

            email.setFrom(new InternetAddress(username));
            email.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );

            email.setSubject(subject);
            email.setText(message);

            Transport.send(email);

            System.out.println("Email sent successfully to " + recipientEmail);

        } catch (MessagingException exception) {
            exception.printStackTrace();

            throw new IllegalStateException(
                    "Email could not be sent: " + exception.getMessage(),
                    exception
            );
        }
    }
}