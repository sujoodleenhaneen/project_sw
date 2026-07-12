package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;

/**
 * Tests the rental expiry reminder service using Mockito.
 */
public class RentalReminderServiceTest {

    /**
     * Verifies that an email notification is requested when
     * an active rental expires the following day.
     */
    @Test
    public void checkAndSendReminder_whenRentalExpiresTomorrow_shouldSendNotification() {


        NotificationService mockNotificationService =
                mock(NotificationService.class);

        RentalReminderService reminderService =
                new RentalReminderService(mockNotificationService);

        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40.0,
                VehicleStatus.RENTED
        );

        Rental rental = new Rental(
                "R1",
                vehicle,
                "Haneen",
                "haneen@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

  
        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

  
        assertTrue(reminderGenerated);

        verify(mockNotificationService, times(1))
                .sendNotification(
                        eq("haneen@example.com"),
                        eq("Rental Expiry Reminder"),
                        contains("expires on 2026-07-15")
                );
    }

    /**
     * Verifies that no notification is requested when
     * the rental expiry date is still several days away.
     */
    @Test
    public void checkAndSendReminder_whenExpiryIsFar_shouldNotSendNotification() {

  
        NotificationService mockNotificationService =
                mock(NotificationService.class);

        RentalReminderService reminderService =
                new RentalReminderService(mockNotificationService);

        Vehicle vehicle = new Vehicle(
                "V2",
                "Honda",
                "Civic",
                45.0,
                VehicleStatus.RENTED
        );

        Rental rental = new Rental(
                "R2",
                vehicle,
                "Haneen",
                "haneen@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 20),
                RentalStatus.ACTIVE
        );

 
        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );


        assertFalse(reminderGenerated);
        verifyNoInteractions(mockNotificationService);
    }

    /**
     * Verifies that no notification is requested for a closed rental.
     */
    @Test
    public void checkAndSendReminder_whenRentalIsClosed_shouldNotSendNotification() {

   
        NotificationService mockNotificationService =
                mock(NotificationService.class);

        RentalReminderService reminderService =
                new RentalReminderService(mockNotificationService);

        Vehicle vehicle = new Vehicle(
                "V3",
                "Ford",
                "Focus",
                50.0,
                VehicleStatus.AVAILABLE
        );

        Rental rental = new Rental(
                "R3",
                vehicle,
                "Haneen",
                "haneen@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                RentalStatus.CLOSED
        );

       
        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

       
        assertFalse(reminderGenerated);
        verifyNoInteractions(mockNotificationService);
    }
}