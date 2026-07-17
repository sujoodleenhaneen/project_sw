package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.application.RentalService;
import com.vrms.domain.Rental;

/**
 * Tests rental requests handled by {@link RentalController}.
 */
public class RentalControllerTest {

    /**
     * Mocked rental service used during testing.
     */
    private RentalService rentalService;

    /**
     * Controller under test.
     */
    private RentalController rentalController;

    /**
     * Creates the mock service and controller before each test.
     */
    @BeforeEach
    public void setUp() {
        rentalService = mock(RentalService.class);
        rentalController = new RentalController(rentalService);
    }

    /**
     * Verifies that the controller sends all rental data to
     * the service and returns the created rental.
     */
    @Test
    public void rentVehicle_whenRequestIsValid_shouldReturnCreatedRental() {

        LocalDate startDate = LocalDate.of(2026, 7, 10);
        LocalDate endDate = LocalDate.of(2026, 7, 15);

        Rental expectedRental = mock(Rental.class);

        when(rentalService.rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                startDate,
                endDate
        )).thenReturn(expectedRental);

        Rental actualRental = rentalController.rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                startDate,
                endDate
        );

        assertSame(expectedRental, actualRental);

        verify(rentalService).rentVehicle(
                "R1",
                "V1",
                "Ahmad",
                "ahmad@example.com",
                startDate,
                endDate
        );
    }
    @Test
    public void returnVehicle_shouldReturnServiceResult() {
        Rental expectedRental = mock(Rental.class);

        when(
                rentalService.returnVehicle("V1")
        ).thenReturn(expectedRental);

        Rental actualRental =
                rentalController.returnVehicle("V1");

        assertSame(expectedRental, actualRental);

        verify(rentalService).returnVehicle("V1");
    }
}