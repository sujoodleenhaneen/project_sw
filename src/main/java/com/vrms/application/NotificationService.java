package com.vrms.application;


public interface NotificationService {

    
    void sendNotification(
            String recipientEmail,
            String subject,
            String message
    );
}