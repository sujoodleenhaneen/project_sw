package com.vrms.domain;

import java.time.LocalDate;

/**
 * Represents a rental record for a vehicle in the Vehicle Rental Management System.
 *
 * <p>A rental record stores the rented vehicle, customer information, rental start
 * and end dates, the current rental status, and the total cost after the vehicle
 * is returned.</p>
 */
public class Rental {

    /**
     * Unique identifier for the rental record.
     */
    private String rentalId;

    /**
     * The vehicle rented by the customer.
     */
    private Vehicle vehicle;

    /**
     * The name of the customer who rented the vehicle.
     */
    private String customerName;

    /**
     * The email address of the customer.
     *
     * <p>This email may be used for rental notifications or reminders.</p>
     */
    private String customerEmail;

    /**
     * The start date of the rental period.
     */
    private LocalDate startDate;

    /**
     * The expected end date of the rental period.
     */
    private LocalDate endDate;

    /**
     * The current status of the rental.
     *
     * <p>For example, the rental may be active while the vehicle is rented,
     * and closed after the vehicle is returned.</p>
     */
    private RentalStatus status;

    /**
     * The total cost of the rental.
     *
     * <p>This value is usually calculated when the vehicle is returned. It may
     * include the normal rental cost and any late return penalty.</p>
     */
    private double totalCost;

    /**
     * Creates a new rental record.
     *
     * @param rentalId unique identifier for the rental
     * @param vehicle the vehicle being rented
     * @param customerName the name of the customer
     * @param customerEmail the email address of the customer
     * @param startDate the rental start date
     * @param endDate the expected rental end date
     * @param status the current rental status
     */
    public Rental(String rentalId, Vehicle vehicle, String customerName,
                  String customerEmail, LocalDate startDate,
                  LocalDate endDate, RentalStatus status) {

        this.rentalId = rentalId;
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.totalCost = 0;
    }

    /**
     * Returns the rental identifier.
     *
     * @return the rental identifier
     */
    public String getRentalId() {
        return rentalId;
    }

    /**
     * Returns the vehicle rented by the customer.
     *
     * @return the rented vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the name of the customer who rented the vehicle.
     *
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the email address of the customer.
     *
     * @return the customer email address
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Returns the rental start date.
     *
     * @return the rental start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the expected rental end date.
     *
     * @return the rental end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the current rental status.
     *
     * @return the rental status
     */
    public RentalStatus getStatus() {
        return status;
    }

    /**
     * Sets the total cost of the rental.
     *
     * <p>This method is usually called after calculating the rental cost and any
     * late return penalty during the vehicle return process.</p>
     *
     * @param totalCost the calculated total rental cost
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Returns the total cost of the rental.
     *
     * @return the total rental cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Closes the rental record when the vehicle is returned.
     *
     * <p>After this method is called, the rental status becomes
     * {@link RentalStatus#CLOSED}.</p>
     */
    public void closeRental() {
        this.status = RentalStatus.CLOSED;
    }
}