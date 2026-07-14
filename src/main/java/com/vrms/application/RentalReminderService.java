package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.persistence.RentalRepository;

public class RentalReminderService {

    private static final int REMINDER_DAYS_BEFORE_EXPIRY = 2;

    private final List<NotificationService> observers;
    private final RentalRepository rentalRepository;

    public RentalReminderService(NotificationService notificationService) {
        this(notificationService, null);
    }

    public RentalReminderService(NotificationService notificationService, RentalRepository rentalRepository) {
        this.observers = new ArrayList<>();
        this.rentalRepository = rentalRepository;
        addObserver(notificationService);
    }

    public void addObserver(NotificationService observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Notification observer cannot be null.");
        }

        observers.add(observer);
    }

    public void removeObserver(NotificationService observer) {
        observers.remove(observer);
    }

    public int checkAllRentalsAndSendReminders(LocalDate currentDate) {
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null.");
        }

        if (rentalRepository == null) {
            throw new IllegalStateException("Rental repository is not configured.");
        }

        int remindersGenerated = 0;

        for (Rental rental : rentalRepository.findAll()) {
            if (checkAndSendReminder(rental, currentDate)) {
                remindersGenerated++;
            }
        }

        return remindersGenerated;
    }

    public boolean checkAndSendReminder(Rental rental, LocalDate currentDate) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental cannot be null.");
        }

        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null.");
        }

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            return false;
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(currentDate, rental.getEndDate());

        String subject;
        String message;

        if (daysUntilExpiry == REMINDER_DAYS_BEFORE_EXPIRY) {
            subject = "Rental Expiry Reminder";

            message = "Hello "
                    + rental.getCustomerName()
                    + ", your rental for "
                    + rental.getVehicle().getBrand()
                    + " "
                    + rental.getVehicle().getModel()
                    + " will expire in two days on "
                    + rental.getEndDate()
                    + ".";
        } else if (daysUntilExpiry == 0) {
            subject = "Rental Expired";

            message = "Hello "
                    + rental.getCustomerName()
                    + ", your rental for "
                    + rental.getVehicle().getBrand()
                    + " "
                    + rental.getVehicle().getModel()
                    + " expires today.";
        } else {
            return false;
        }

        notifyObservers(rental.getCustomerEmail(), subject, message);
        return true;
    }

    private void notifyObservers(String recipientEmail, String subject, String message) {
        for (NotificationService observer : observers) {
            observer.sendNotification(recipientEmail, subject, message);
        }
    }
}