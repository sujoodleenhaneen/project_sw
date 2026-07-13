package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.InMemoryRentalRepository;
import com.vrms.persistence.RentalRepository;

public class RentalReminderServiceTest {

    private NotificationService notificationService;

    private RentalReminderService reminderService;
    
    @BeforeEach
    public void setUp() {
        notificationService = mock(NotificationService.class);
        reminderService =
                new RentalReminderService(notificationService);
    }

   
    @Test
    public void checkReminder_expiresTomorrow_sendsNotification() {

        Rental rental = createRental(
                "R1",
                "haneen@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService, times(1))
                .sendNotification(
                        eq("haneen@example.com"),
                        eq("Rental Expiry Reminder"),
                        contains("expires on 2026-07-15")
                );
    }

    @Test
    public void checkReminder_expiresToday_sendsNotification() {

        Rental rental = createRental(
                "R2",
                "sara@example.com",
                LocalDate.of(2026, 7, 14),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService).sendNotification(
                eq("sara@example.com"),
                eq("Rental Expiry Reminder"),
                contains("expires on 2026-07-14")
        );
    }

    @Test
    public void checkReminder_expiryIsFar_doesNotSendNotification() {

        Rental rental = createRental(
                "R3",
                "omar@example.com",
                LocalDate.of(2026, 7, 20),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertFalse(reminderGenerated);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void checkReminder_alreadyExpired_doesNotSendNotification() {

        Rental rental = createRental(
                "R4",
                "lina@example.com",
                LocalDate.of(2026, 7, 10),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertFalse(reminderGenerated);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void checkReminder_closedRental_doesNotSendNotification() {

        Rental rental = createRental(
                "R5",
                "mona@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.CLOSED
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertFalse(reminderGenerated);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void checkAllRentals_eligibleRentals_returnsReminderCount() {

        RentalRepository rentalRepository =
                new InMemoryRentalRepository();

        rentalRepository.save(createRental(
                "R6",
                "customer1@example.com",
                LocalDate.of(2026, 7, 14),
                RentalStatus.ACTIVE
        ));

        rentalRepository.save(createRental(
                "R7",
                "customer2@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        ));

        rentalRepository.save(createRental(
                "R8",
                "customer3@example.com",
                LocalDate.of(2026, 7, 20),
                RentalStatus.ACTIVE
        ));

        rentalRepository.save(createRental(
                "R9",
                "customer4@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.CLOSED
        ));

        RentalReminderService serviceWithRepository =
                new RentalReminderService(
                        notificationService,
                        rentalRepository
                );

        int remindersGenerated =
                serviceWithRepository
                        .checkAllRentalsAndSendReminders(
                                LocalDate.of(2026, 7, 14)
                        );

        assertEquals(2, remindersGenerated);

        verify(notificationService, times(2))
                .sendNotification(
                        anyString(),
                        eq("Rental Expiry Reminder"),
                        contains("expires on")
                );
    }

    @Test
    public void addObserver_secondObserver_notifiesBothObservers() {

        NotificationService secondObserver =
                mock(NotificationService.class);

        reminderService.addObserver(secondObserver);

        Rental rental = createRental(
                "R10",
                "rawan@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService).sendNotification(
                eq("rawan@example.com"),
                eq("Rental Expiry Reminder"),
                contains("expires on 2026-07-15")
        );

        verify(secondObserver).sendNotification(
                eq("rawan@example.com"),
                eq("Rental Expiry Reminder"),
                contains("expires on 2026-07-15")
        );
    }

    @Test
    public void removeObserver_removedObserver_isNotNotified() {

        NotificationService secondObserver =
                mock(NotificationService.class);

        reminderService.addObserver(secondObserver);
        reminderService.removeObserver(secondObserver);

        Rental rental = createRental(
                "R11",
                "dana@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

        boolean reminderGenerated =
                reminderService.checkAndSendReminder(
                        rental,
                        LocalDate.of(2026, 7, 14)
                );

        assertTrue(reminderGenerated);

        verify(notificationService).sendNotification(
                eq("dana@example.com"),
                eq("Rental Expiry Reminder"),
                contains("expires on 2026-07-15")
        );

        verifyNoInteractions(secondObserver);
    }

    @Test
    public void addObserver_nullObserver_throwsException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> reminderService.addObserver(null)
        );
    }

    /**
     * Verifies that a null rental is rejected.
     */
    @Test
    public void checkReminder_nullRental_throwsException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> reminderService.checkAndSendReminder(
                        null,
                        LocalDate.of(2026, 7, 14)
                )
        );
    }

    @Test
    public void checkReminder_nullCurrentDate_throwsException() {

        Rental rental = createRental(
                "R12",
                "aya@example.com",
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> reminderService.checkAndSendReminder(
                        rental,
                        null
                )
        );
    }
    @Test
    public void checkAllRentals_nullCurrentDate_throwsException() {

        RentalRepository rentalRepository =
                new InMemoryRentalRepository();

        RentalReminderService serviceWithRepository =
                new RentalReminderService(
                        notificationService,
                        rentalRepository
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> serviceWithRepository
                        .checkAllRentalsAndSendReminders(null)
        );
    }

    @Test
    public void checkAllRentals_repositoryNotConfigured_throwsException() {

        assertThrows(
                IllegalStateException.class,
                () -> reminderService
                        .checkAllRentalsAndSendReminders(
                                LocalDate.of(2026, 7, 14)
                        )
        );
    }

    /**
     * Creates a rental object used during reminder tests.
     *
     * @param rentalId rental identifier
     * @param customerEmail customer email
     * @param endDate rental expiry date
     * @param status rental status
     * @return created rental
     */
    private Rental createRental(
            String rentalId,
            String customerEmail,
            LocalDate endDate,
            RentalStatus status) {

        VehicleStatus vehicleStatus =
                status == RentalStatus.ACTIVE
                        ? VehicleStatus.RENTED
                        : VehicleStatus.AVAILABLE;

        Vehicle vehicle = new Vehicle(
                "V-" + rentalId,
                "Toyota",
                "Corolla",
                40.0,
                vehicleStatus
        );

        return new Rental(
                rentalId,
                vehicle,
                "Customer",
                customerEmail,
                LocalDate.of(2026, 7, 1),
                endDate,
                status
        );
    }
}