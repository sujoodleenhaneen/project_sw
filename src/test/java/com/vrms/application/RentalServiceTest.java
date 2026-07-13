package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.InMemoryRentalRepository;
import com.vrms.persistence.InMemoryVehicleRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

/**
 * Tests the vehicle rental operations provided by {@link RentalService}.
 */
public class RentalServiceTest {

    /**
     * Repository containing vehicle records used during testing.
     */
    private VehicleRepository vehicleRepository;

    /**
     * Repository containing rental records used during testing.
     */
    private RentalRepository rentalRepository;

    /**
     * Service under test.
     */
    private RentalService rentalService;

    /**
     * Creates fresh repositories and a fresh rental service before each test.
     */
    @BeforeEach
    public void setUp() {

        vehicleRepository =
                new InMemoryVehicleRepository();

        rentalRepository =
                new InMemoryRentalRepository();

        rentalService =
                new RentalService(
                        vehicleRepository,
                        rentalRepository
                );
    }

    /**
     * Verifies that renting an available vehicle creates a rental,
     * stores it, and changes the vehicle status to rented.
     */
    @Test
    public void rentVehicle_whenVehicleIsAvailable_shouldCreateRentalAndChangeStatus() {

        Rental rental = rentalService.rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        assertEquals(
                "R1",
                rental.getRentalId()
        );

        assertEquals(
                "Ahmad",
                rental.getCustomerName()
        );

        assertEquals(
                "ahmad@example.com",
                rental.getCustomerEmail()
        );

        assertEquals(
                RentalStatus.ACTIVE,
                rental.getStatus()
        );

        assertEquals(
                VehicleStatus.RENTED,
                rental.getVehicle().getStatus()
        );

        assertEquals(
                1,
                rentalRepository.findAll().size()
        );
    }

    /**
     * Verifies that a vehicle whose status is already rented
     * cannot be rented again.
     */
    @Test
    public void rentVehicle_whenVehicleStatusIsRented_shouldThrowException() {

        Vehicle vehicle =
                vehicleRepository.findAll().get(0);

        vehicle.setStatus(VehicleStatus.RENTED);

        assertThrows(
                IllegalStateException.class,
                () -> rentalService.rentVehicle(
                        "R2",
                        vehicle.getId(),
                        "Sara",
                        "sara@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 15)
                )
        );
    }

    /**
     * Verifies that an unknown vehicle identifier is rejected.
     */
    @Test
    public void rentVehicle_whenVehicleDoesNotExist_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R3",
                        "V99",
                        "Omar",
                        "omar@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 15)
                )
        );
    }

    /**
     * Verifies that a null rental start date is rejected.
     */
    @Test
    public void rentVehicle_whenStartDateIsNull_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R4",
                        "V1",
                        "Lina",
                        "lina@example.com",
                        null,
                        LocalDate.of(2026, 7, 15)
                )
        );
    }

    /**
     * Verifies that a null rental end date is rejected.
     */
    @Test
    public void rentVehicle_whenEndDateIsNull_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R5",
                        "V1",
                        "Mona",
                        "mona@example.com",
                        LocalDate.of(2026, 7, 10),
                        null
                )
        );
    }

    /**
     * Verifies that an end date before the start date is rejected.
     */
    @Test
    public void rentVehicle_whenEndDateIsBeforeStartDate_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R6",
                        "V1",
                        "Khaled",
                        "khaled@example.com",
                        LocalDate.of(2026, 7, 15),
                        LocalDate.of(2026, 7, 10)
                )
        );
    }

    /**
     * Verifies that equal start and end dates are rejected.
     */
    @Test
    public void rentVehicle_whenEndDateEqualsStartDate_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R7",
                        "V1",
                        "Noor",
                        "noor@example.com",
                        LocalDate.of(2026, 7, 10),
                        LocalDate.of(2026, 7, 10)
                )
        );
    }

    /**
     * Verifies that a rental longer than thirty days is rejected.
     */
    @Test
    public void rentVehicle_whenRentalPeriodExceedsThirtyDays_shouldThrowException() {

        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicle(
                        "R8",
                        "V1",
                        "Huda",
                        "huda@example.com",
                        LocalDate.of(2026, 7, 1),
                        LocalDate.of(2026, 8, 15)
                )
        );
    }

    /**
     * Verifies that a rental lasting exactly thirty days is accepted.
     */
    @Test
    public void rentVehicle_whenRentalPeriodIsExactlyThirtyDays_shouldCreateRental() {

        Rental rental = rentalService.rentVehicle(
                "R9",
                "V1",
                "Rana",
                "rana@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31)
        );

        assertEquals(
                RentalStatus.ACTIVE,
                rental.getStatus()
        );

        assertEquals(
                VehicleStatus.RENTED,
                rental.getVehicle().getStatus()
        );

        assertEquals(
                1,
                rentalRepository.findAll().size()
        );
    }

    /**
     * Verifies that an active rental prevents double booking
     * of the same vehicle.
     */
    @Test
    public void rentVehicle_whenVehicleHasActiveRental_shouldThrowException() {

        Vehicle vehicle =
                vehicleRepository.findAll().get(0);

        Rental existingRental = new Rental(
                "R10",
                vehicle,
                "Rawan",
                "rawan@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                RentalStatus.ACTIVE
        );

        rentalRepository.save(existingRental);

        assertThrows(
                IllegalStateException.class,
                () -> rentalService.rentVehicle(
                        "R11",
                        vehicle.getId(),
                        "Dana",
                        "dana@example.com",
                        LocalDate.of(2026, 7, 6),
                        LocalDate.of(2026, 7, 10)
                )
        );
    }

    /**
     * Verifies that a closed previous rental does not prevent
     * creating a new rental for the same vehicle.
     */
    @Test
    public void rentVehicle_whenPreviousRentalIsClosed_shouldCreateNewRental() {

        Vehicle vehicle =
                vehicleRepository.findAll().get(0);

        Rental closedRental = new Rental(
                "R12",
                vehicle,
                "Lama",
                "lama@example.com",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                RentalStatus.CLOSED
        );

        rentalRepository.save(closedRental);
        vehicle.setStatus(VehicleStatus.AVAILABLE);

        Rental newRental = rentalService.rentVehicle(
                "R13",
                vehicle.getId(),
                "Aya",
                "aya@example.com",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15)
        );

        assertEquals(
                RentalStatus.ACTIVE,
                newRental.getStatus()
        );

        assertEquals(
                VehicleStatus.RENTED,
                vehicle.getStatus()
        );

        assertEquals(
                2,
                rentalRepository.findAll().size()
        );
    }
}