package com.vrms.presentation;

import java.time.LocalDate;

import com.vrms.application.RentalService;
import com.vrms.domain.Rental;
import com.vrms.domain.RentalValidationData;

public class RentalController {

    private final RentalService rentalService;

    public RentalController(
            RentalService rentalService) {

        this.rentalService = rentalService;
    }

    public Rental rentVehicle(
            String rentalId,
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

    public Rental rentVehicle(
            String rentalId,
            String vehicleId,
            String customerName,
            String customerEmail,
            LocalDate startDate,
            LocalDate endDate,
            RentalValidationData validationData) {

        return rentalService.rentVehicle(
                rentalId,
                vehicleId,
                customerName,
                customerEmail,
                startDate,
                endDate,
                validationData
        );
    }

    public Rental returnVehicle(String vehicleId) {
        return rentalService.returnVehicle(vehicleId);
    }
}