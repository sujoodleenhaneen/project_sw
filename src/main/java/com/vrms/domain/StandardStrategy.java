package com.vrms.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Standard rental cost calculation strategy.
 *
 * <p>This class implements the {@link RentalCostStrategy} interface and provides
 * the default way to calculate the total rental cost for a returned vehicle.</p>
 *
 * <p>The strategy calculates the basic rental cost using the vehicle price per day
 * and rental duration. It also adds a fixed late return cost when the vehicle is
 * returned after the expected return date.</p>
 */
public class StandardStrategy implements RentalCostStrategy {

    /**
     * Fixed late return cost applied per late day.
     */
    private static final double LATE_COST = 20.0;

    /**
     * Calculates the total rental cost for a rental.
     *
     * <p>The calculation is based on the vehicle price per day and the planned
     * rental duration. If the vehicle is returned late, an additional late cost is
     * added for each late day.</p>
     *
     * @param rental the rental record that contains vehicle and rental date information
     * @param returnDate the actual date when the vehicle is returned
     * @return the total calculated rental cost
     * @throws NullPointerException if rental, vehicle, rental dates, or return date are null
     */
    @Override
    public double calculateCost(Rental rental, LocalDate returnDate) {
        long plannedDays = ChronoUnit.DAYS.between(returnDate, LocalDate.now());
        double totalCost = rental.getVehicle().getPricePerDay() * plannedDays;

        if (returnDate.isAfter(LocalDate.now())) {
            long days = ChronoUnit.DAYS.between(returnDate, rental.getEndDate());
            totalCost += days * LATE_COST;
        }

        return totalCost;
    }
}