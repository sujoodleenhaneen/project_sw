package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

public class RentalService {

    private VehicleRepository vehicleRepository;
    private RentalRepository rentalRepository;

    private static final int MAX_RENTAL_DAYS = 30;

    public RentalService(VehicleRepository vehicleRepository,
                         RentalRepository rentalRepository) {

        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    public Rental rentVehicle(String rentalId,
                              String vehicleId,
                              String customerName,
                              String customerEmail,
                              LocalDate startDate,
                              LocalDate endDate) {

        validateRentalPeriod(startDate, endDate);

        Vehicle vehicle = findVehicleById(vehicleId);

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Vehicle is not available for rental."
            );
        }

        if (hasActiveRental(vehicleId)) {
            throw new IllegalStateException(
                    "Vehicle is already rented."
            );
        }

        Rental rental = new Rental(
                rentalId,
                vehicle,
                customerName,
                customerEmail,
                startDate,
                endDate,
                RentalStatus.ACTIVE
        );

        vehicle.setStatus(VehicleStatus.RENTED);
        rentalRepository.save(rental);

        return rental;
    }

    private Vehicle findVehicleById(String vehicleId) {

        List<Vehicle> vehicles = vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(vehicleId)) {
                return vehicle;
            }
        }

        throw new IllegalArgumentException("Vehicle not found.");
    }

    private boolean hasActiveRental(String vehicleId) {

        List<Rental> rentals = rentalRepository.findAll();

        for (Rental rental : rentals) {
            if (rental.getVehicle().getId().equals(vehicleId)
                    && rental.getStatus() == RentalStatus.ACTIVE) {

                return true;
            }
        }

        return false;
    }

    private void validateRentalPeriod(LocalDate startDate,
                                      LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Rental dates cannot be null."
            );
        }

        if (endDate.isBefore(startDate)
                || endDate.isEqual(startDate)) {

            throw new IllegalArgumentException(
                    "End date must be after start date."
            );
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);

        if (days > MAX_RENTAL_DAYS) {
            throw new IllegalArgumentException(
                    "Rental period cannot exceed 30 days."
            );
        }
    }
}