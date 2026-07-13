package com.vrms.application;

/**
 * Defines the contract for sending notifications to customers.
 */
public interface NotificationService {

    /**
     * Sends a notification to a customer.
     *
     * @param recipientEmail the customer's email address
     * @param subject the notification subject
     * @param message the notification message
     */
    void sendNotification(
            String recipientEmail,
            String subject,
            String message
    );
}