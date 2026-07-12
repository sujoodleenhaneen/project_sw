package com.vrms.presentation;

import java.time.LocalDate;

import com.vrms.application.RentalService;
import com.vrms.domain.Rental;

/**
 * Handles vehicle rental requests from the presentation layer.
 */
public class RentalController {

    /**
     * Service responsible for rental operations.
     */
    private final RentalService rentalService;

    /**
     * Creates a rental controller.
     *
     * @param rentalService service responsible for rental operations
     */
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * Requests the creation of a new vehicle rental.
     *
     * @param rentalId unique rental identifier
     * @param vehicleId identifier of the selected vehicle
     * @param customerName customer's name
     * @param customerEmail customer's email
     * @param startDate rental start date
     * @param endDate rental end date
     * @return the created rental
     */
    public Rental rentVehicle(String rentalId,
                              String vehicleId,
                              String customerName,
                              String customerEmail,
                              LocalDate startDate,
                              LocalDate endDate) {

        return rentalService.rentVehicle(
                rentalId,
                vehicleId,
                customerName,
                customerEmail,
                startDate,
                endDate
        );
    }
}