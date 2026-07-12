package com.vrms.application;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration test for the real email notification service.
 *
 * <p>This test is disabled during normal unit testing because it requires
 * valid Gmail environment variables and an internet connection.</p>
 */
@Disabled("Requires GMAIL_ADDRESS and GMAIL_APP_PASSWORD environment variables")
public class EmailNotificationServiceTest {

    /**
     * Sends a real rental expiry reminder email.
     *
     * <p>Enable this test manually only when Gmail environment variables
     * are configured.</p>
     */
    @Test
    public void sendNotification_shouldSendRealEmail() {

        NotificationService notificationService =
                new EmailNotificationService();

        notificationService.sendNotification(
                "haneen@example.com",
                "Rental Expiry Reminder",
                "Your rental expires tomorrow."
        );
    }
}