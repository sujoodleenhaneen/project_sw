package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.persistence.RentalRepository;

/**
 * Checks active rentals and generates expiry reminders.
 * Notification services are registered as observers and are notified
 * when a rental is close to its expiry date.
 */
public class RentalReminderService {

    /**
     * Number of days before expiry when a reminder should be generated.
     */
    private static final int REMINDER_DAYS_BEFORE_EXPIRY = 1;

    /**
     * Registered notification observers.
     */
    private final List<NotificationService> observers;

    /**
     * Repository used to retrieve rental records.
     */
    private final RentalRepository rentalRepository;

    /**
     * Creates a reminder service without a rental repository.
     * This constructor is useful when checking one rental directly,
     * especially during unit testing.
     *
     * @param notificationService the notification service to register
     */
    public RentalReminderService(
            NotificationService notificationService) {

        this(notificationService, null);
    }

    /**
     * Creates a reminder service that can check all stored rentals.
     *
     * @param notificationService the notification service to register
     * @param rentalRepository repository containing rental records
     */
    public RentalReminderService(
            NotificationService notificationService,
            RentalRepository rentalRepository) {

        this.observers = new ArrayList<>();
        this.rentalRepository = rentalRepository;

        addObserver(notificationService);
    }

    /**
     * Registers a notification observer.
     *
     * @param observer the notification service to register
     */
    public void addObserver(NotificationService observer) {

        if (observer == null) {
            throw new IllegalArgumentException(
                    "Notification observer cannot be null."
            );
        }

        observers.add(observer);
    }

    /**
     * Removes a notification observer.
     *
     * @param observer the notification service to remove
     */
    public void removeObserver(NotificationService observer) {
        observers.remove(observer);
    }

    /**
     * Checks all stored rentals and sends reminders for rentals
     * that expire today or the following day.
     *
     * @param currentDate the date used during the reminder check
     * @return number of reminders generated
     */
    public int checkAllRentalsAndSendReminders(
            LocalDate currentDate) {

        if (currentDate == null) {
            throw new IllegalArgumentException(
                    "Current date cannot be null."
            );
        }

        if (rentalRepository == null) {
            throw new IllegalStateException(
                    "Rental repository is not configured."
            );
        }

        int remindersGenerated = 0;

        for (Rental rental : rentalRepository.findAll()) {

            if (checkAndSendReminder(rental, currentDate)) {
                remindersGenerated++;
            }
        }

        return remindersGenerated;
    }

    /**
     * Checks whether one rental is close to its expiry date.
     * If the active rental expires today or tomorrow,
     * a reminder is generated.
     *
     * @param rental the rental to check
     * @param currentDate the date used during the reminder check
     * @return true if a reminder was generated; otherwise false
     */
    public boolean checkAndSendReminder(
            Rental rental,
            LocalDate currentDate) {

        if (rental == null) {
            throw new IllegalArgumentException(
                    "Rental cannot be null."
            );
        }

        if (currentDate == null) {
            throw new IllegalArgumentException(
                    "Current date cannot be null."
            );
        }

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            return false;
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(
                currentDate,
                rental.getEndDate()
        );

        if (daysUntilExpiry >= 0
                && daysUntilExpiry
                <= REMINDER_DAYS_BEFORE_EXPIRY) {

            String subject =
                    "Rental Expiry Reminder";

            String message =
                    "Hello "
                    + rental.getCustomerName()
                    + ", your rental for "
                    + rental.getVehicle().getBrand()
                    + " "
                    + rental.getVehicle().getModel()
                    + " expires on "
                    + rental.getEndDate()
                    + ".";

            notifyObservers(
                    rental.getCustomerEmail(),
                    subject,
                    message
            );

            return true;
        }

        return false;
    }

    /**
     * Notifies all registered observers.
     *
     * @param recipientEmail the customer's email address
     * @param subject the notification subject
     * @param message the notification message
     */
    private void notifyObservers(
            String recipientEmail,
            String subject,
            String message) {

        for (NotificationService observer : observers) {

            observer.sendNotification(
                    recipientEmail,
                    subject,
                    message
            );
        }
    }
}