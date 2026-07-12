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

public class RentalServiceTest {

    private VehicleRepository vehicleRepository;
    private RentalRepository rentalRepository;
    private RentalService rentalService;

    @BeforeEach
    public void setUp() {
        vehicleRepository = new InMemoryVehicleRepository();
        rentalRepository = new InMemoryRentalRepository();
        rentalService = new RentalService(
                vehicleRepository,
                rentalRepository
        );
    }

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

        assertEquals("R1", rental.getRentalId());
        assertEquals("Ahmad", rental.getCustomerName());
        assertEquals("ahmad@example.com", rental.getCustomerEmail());
        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
        assertEquals(
                VehicleStatus.RENTED,
                rental.getVehicle().getStatus()
        );
        assertEquals(1, rentalRepository.findAll().size());
    }

    @Test
    public void rentVehicle_whenVehicleIsAlreadyRented_shouldThrowException() {

        assertThrows(IllegalStateException.class, () -> {
            rentalService.rentVehicle(
                    "R2",
                    "V2",
                    "Sara",
                    "sara@example.com",
                    LocalDate.of(2026, 7, 10),
                    LocalDate.of(2026, 7, 15)
            );
        });
    }

    @Test
    public void rentVehicle_whenVehicleDoesNotExist_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentVehicle(
                    "R3",
                    "V99",
                    "Omar",
                    "omar@example.com",
                    LocalDate.of(2026, 7, 10),
                    LocalDate.of(2026, 7, 15)
            );
        });
    }

    @Test
    public void rentVehicle_whenStartDateIsNull_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentVehicle(
                    "R4",
                    "V1",
                    "Lina",
                    "lina@example.com",
                    null,
                    LocalDate.of(2026, 7, 15)
            );
        });
    }

    @Test
    public void rentVehicle_whenEndDateIsNull_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentVehicle(
                    "R5",
                    "V1",
                    "Mona",
                    "mona@example.com",
                    LocalDate.of(2026, 7, 10),
                    null
            );
        });
    }

    @Test
    public void rentVehicle_whenEndDateIsBeforeStartDate_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentVehicle(
                    "R6",
                    "V1",
                    "Khaled",
                    "khaled@example.com",
                    LocalDate.of(2026, 7, 15),
                    LocalDate.of(2026, 7, 10)
            );
        });
    }

    @Test
    public void rentVehicle_whenEndDateEqualsStartDate_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentVehicle(
                    "R7",
                    "V1",
                    "Noor",
                    "noor@example.com",
                    LocalDate.of(2026, 7, 10),
                    LocalDate.of(2026, 7, 10)
            );
        });
    }

    @Test
    public void rentVehicle_whenRentalPeriodExceedsThirtyDays_shouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> {
            rentalService.rentVehicle(
                    "R8",
                    "V1",
                    "Huda",
                    "huda@example.com",
                    LocalDate.of(2026, 7, 1),
                    LocalDate.of(2026, 8, 15)
            );
        });
    }

    @Test
    public void rentVehicle_whenVehicleHasActiveRental_shouldThrowException() {

        Vehicle vehicle = vehicleRepository.findAll().get(0);

        Rental existingRental = new Rental(
                "R9",
                vehicle,
                "Rawan",
                "rawan@example.com",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 5),
                RentalStatus.ACTIVE
        );

        rentalRepository.save(existingRental);

        assertThrows(IllegalStateException.class, () -> {
            rentalService.rentVehicle(
                    "R10",
                    vehicle.getId(),
                    "Dana",
                    "dana@example.com",
                    LocalDate.of(2026, 7, 6),
                    LocalDate.of(2026, 7, 10)
            );
        });
    }
}