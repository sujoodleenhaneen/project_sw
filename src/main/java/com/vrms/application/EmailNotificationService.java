package com.vrms.application;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Sends rental notifications to customers using Gmail SMTP.
 */
public class EmailNotificationService implements NotificationService {

    /**
     * Gmail SMTP server address.
     */
    private static final String SMTP_HOST = "smtp.gmail.com";

    /**
     * Gmail SMTP port for STARTTLS.
     */
    private static final String SMTP_PORT = "587";

    /**
     * Sends an email notification to the specified customer.
     *
     * @param recipientEmail the customer's email address
     * @param subject the email subject
     * @param message the email message
     */
    @Override
    public void sendNotification(String recipientEmail,
                                 String subject,
                                 String message) {

        String senderEmail = System.getenv("GMAIL_ADDRESS");
        String appPassword = System.getenv("GMAIL_APP_PASSWORD");

        if (senderEmail == null || senderEmail.trim().isEmpty()) {
            throw new IllegalStateException(
                    "GMAIL_ADDRESS environment variable is missing."
            );
        }

        if (appPassword == null || appPassword.trim().isEmpty()) {
            throw new IllegalStateException(
                    "GMAIL_APP_PASSWORD environment variable is missing."
            );
        }

        String cleanedPassword = appPassword.replace(" ", "");

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                senderEmail,
                                cleanedPassword
                        );
                    }
                }
        );

        try {
            Message email = new MimeMessage(session);

            email.setFrom(new InternetAddress(senderEmail));
            email.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );

            email.setSubject(subject);
            email.setText(message);

            Transport.send(email);

            System.out.println(
                    "Real email sent successfully to: "
                            + recipientEmail
            );

        } catch (MessagingException exception) {
            throw new IllegalStateException(
                    "Email could not be sent: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
}