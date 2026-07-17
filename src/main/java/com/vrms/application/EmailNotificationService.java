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

/**
 * Sends email notifications to customers using Gmail SMTP.
 *
 * <p>The email username and password are loaded from environment
 * variables or from an optional .env file.</p>
 */
public class EmailNotificationService
        implements NotificationService {

    /**
     * Email account used to send notifications.
     */
    private final String username;

    /**
     * Application password used by the email account.
     */
    private final String password;

    /**
     * Creates the email notification service and loads any available
     * email credentials.
     *
     * <p>The application can still start when the .env file is missing.
     * Credentials are checked only when an email is sent.</p>
     */
    public EmailNotificationService() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        this.username = dotenv.get("EMAIL_USERNAME");
        this.password = dotenv.get("EMAIL_PASSWORD");
    }

    /**
     * Sends an email notification to the provided recipient.
     *
     * @param recipientEmail recipient email address
     * @param subject email subject
     * @param message email message
     * @throws IllegalStateException if credentials are missing
     *                               or the email cannot be sent
     */
    @Override
    public void sendNotification(
            String recipientEmail,
            String subject,
            String message) {

        validateCredentials();

        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put(
                "mail.smtp.starttls.enable",
                "true"
        );
        properties.put(
                "mail.smtp.starttls.required",
                "true"
        );
        properties.put(
                "mail.smtp.host",
                "smtp.gmail.com"
        );
        properties.put(
                "mail.smtp.port",
                "587"
        );
        properties.put(
                "mail.smtp.ssl.trust",
                "smtp.gmail.com"
        );

        Session session = Session.getInstance(
                properties,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                            getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                username,
                                password.replace(" ", "")
                        );
                    }
                }
        );

        try {
            Message email = new MimeMessage(session);

            email.setFrom(
                    new InternetAddress(username)
            );

            email.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(
                            recipientEmail
                    )
            );

            email.setSubject(subject);
            email.setText(message);

            Transport.send(email);

            System.out.println(
                    "Email sent successfully to "
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

    /**
     * Checks that the required email credentials are available.
     *
     * @throws IllegalStateException if the username or password
     *                               is missing
     */
    private void validateCredentials() {
        if (username == null
                || username.trim().isEmpty()) {

            throw new IllegalStateException(
                    "EMAIL_USERNAME is missing."
            );
        }

        if (password == null
                || password.trim().isEmpty()) {

            throw new IllegalStateException(
                    "EMAIL_PASSWORD is missing."
            );
        }
    }
}