package com.vrms.application;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vrms.domain.Rental;
import com.vrms.domain.RentalCostStrategy;
import com.vrms.domain.RentalStatus;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

/**
 * Provides the application logic required to rent and return vehicles.
 *
 * <p>This service validates rental periods, prevents double booking, updates
 * vehicle status, saves rental records, closes returned rentals, and calculates
 * rental costs using a rental cost strategy.</p>
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
     * Strategy used to calculate the total rental cost when a vehicle is returned.
     *
     * <p>The strategy allows the billing calculation to be changed without modifying
     * the rental service logic. If no strategy is set, returning a vehicle will fail.</p>
     */
    private RentalCostStrategy rentalStrategy;

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
        this.rentalStrategy = null;
    }

    /**
     * Sets the rental cost calculation strategy.
     *
     * <p>This method is used to inject the pricing strategy that will calculate
     * the total cost of a rental when the vehicle is returned.</p>
     *
     * @param rentalStrategy strategy used to calculate rental cost
     */
    public void setRentalStrategy(RentalCostStrategy rentalStrategy) {
        this.rentalStrategy = rentalStrategy;
    }

    /**
     * Returns the currently used rental cost strategy.
     *
     * @return the rental cost strategy used by this service
     */
    public RentalCostStrategy getRentalStrategy() {
        return rentalStrategy;
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
     * <p>The rental dates must not be null, the end date must be after the start
     * date, and the rental duration must not exceed the maximum allowed duration.</p>
     *
     * @param startDate rental start date
     * @param endDate rental end date
     * @throws IllegalArgumentException if the dates are null, invalid, or exceed the limit
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

    /**
     * Finds a rental record using the rented vehicle identifier.
     *
     * <p>This method searches all rental records and returns the rental that
     * belongs to the given vehicle. It is mainly used when returning a vehicle.</p>
     *
     * @param vehicleId identifier of the rented vehicle
     * @return rental record associated with the given vehicle
     * @throws IllegalArgumentException if no rental record is found for the vehicle
     */
    private Rental findRentalByVehicleId(String vehicleId) {

        List<Rental> rentals = rentalRepository.findAll();

        for (Rental rental : rentals) {
            if (rental.getVehicle().getId().equals(vehicleId) && rental.getStatus() == RentalStatus.ACTIVE) {
                return rental;
            }
        }

        throw new IllegalArgumentException("Rental for vehicle not found.");
    }

    /**
     * Returns a rented vehicle and closes its rental record.
     *
     * <p>The method finds the rental record using the vehicle identifier, calculates
     * the total rental cost using the selected rental cost strategy, closes the rental
     * record, and changes the vehicle status back to {@link VehicleStatus#AVAILABLE}.</p>
     *
     * @param vehicleId identifier of the vehicle being returned
     * @return the closed rental record after calculating the total cost
     * @throws IllegalArgumentException if no rental cost strategy is set or if no rental
     *                                  record is found for the given vehicle
     */
    public Rental returnVehicle(String vehicleId) {

        Rental rental = findRentalByVehicleId(vehicleId);
        LocalDate returnDate = LocalDate.now();

        if (rentalStrategy != null) {
            rental.setTotalCost(rentalStrategy.calculateCost(rental, returnDate));
        } else {
            throw new IllegalArgumentException("No rental cost strategy is set.");
        }

        rental.closeRental();
        rental.getVehicle().setStatus(VehicleStatus.AVAILABLE);

        return rental;
    }
}