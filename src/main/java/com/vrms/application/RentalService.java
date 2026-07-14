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

/**
 * Provides the application logic required to rent vehicles.
 *
 * <p>The service validates the rental period, prevents double booking,
 * updates the vehicle status, and saves the created rental record.</p>
 */
public class RentalService {

    /**
     * Maximum allowed rental duration in days.
     */
    private static final int MAX_RENTAL_DAYS = 30;

    /**
     * Repository used to access vehicle records.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * Repository used to store and retrieve rental records.
     */
    private final RentalRepository rentalRepository;

    /**
     * Creates a rental service.
     *
     * @param vehicleRepository repository containing vehicle records
     * @param rentalRepository repository containing rental records
     */
    public RentalService(
            VehicleRepository vehicleRepository,
            RentalRepository rentalRepository) {

        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
    }

    /**
     * Creates a new vehicle rental.
     *
     * @param rentalId unique rental identifier
     * @param vehicleId identifier of the vehicle to rent
     * @param customerName customer name
     * @param customerEmail customer email address
     * @param startDate rental start date
     * @param endDate rental end date
     * @return the newly created rental
     * @throws IllegalArgumentException if the vehicle or rental dates are invalid
     * @throws IllegalStateException if the vehicle cannot be rented
     */
    public Rental rentVehicle(
            String rentalId,
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
        vehicleRepository.save(vehicle);
        rentalRepository.save(rental);

        return rental;
    }

    /**
     * Finds a vehicle using its identifier.
     *
     * @param vehicleId vehicle identifier
     * @return matching vehicle
     * @throws IllegalArgumentException if the vehicle does not exist
     */
    private Vehicle findVehicleById(String vehicleId) {

        List<Vehicle> vehicles = vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equals(vehicleId)) {
                return vehicle;
            }
        }

        throw new IllegalArgumentException("Vehicle not found.");
    }

    /**
     * Checks whether a vehicle has an active rental.
     *
     * @param vehicleId vehicle identifier
     * @return {@code true} if an active rental exists; otherwise {@code false}
     */
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

    /**
     * Validates the rental start and end dates.
     *
     * @param startDate rental start date
     * @param endDate rental end date
     * @throws IllegalArgumentException if the dates are invalid
     */
    private void validateRentalPeriod(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Rental dates cannot be null."
            );
        }

        if (!endDate.isAfter(startDate)) {
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