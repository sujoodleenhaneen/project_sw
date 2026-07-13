package com.vrms.domain;

import java.time.LocalDate;

/**
 * Represents a rental record for a vehicle in the system.
 * Each rental contains the rented vehicle, customer information,
 * rental dates, and the current rental status.
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
     * The email address used to send rental notifications.
     */
    private String customerEmail;

    /**
     * The start date of the rental period.
     */
    private LocalDate startDate;

    /**
     * The end date of the rental period.
     */
    private LocalDate endDate;

    /**
     * The current status of the rental.
     */
    private RentalStatus status;

    /**
     * Creates a new rental record.
     *
     * @param rentalId unique identifier for the rental
     * @param vehicle the vehicle being rented
     * @param customerName the name of the customer
     * @param customerEmail the email address of the customer
     * @param startDate the rental start date
     * @param endDate the rental end date
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
    }

    /**
     * Returns the rental identifier.
     *
     * @return rental identifier
     */
    public String getRentalId() {
        return rentalId;
    }

    /**
     * Returns the rented vehicle.
     *
     * @return rented vehicle
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Returns the customer name.
     *
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Returns the customer email address.
     *
     * @return customer email address
     */
    public String getCustomerEmail() {
        return customerEmail;
    }

    /**
     * Returns the rental start date.
     *
     * @return rental start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the rental end date.
     *
     * @return rental end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the rental status.
     *
     * @return rental status
     */
    public RentalStatus getStatus() {
        return status;
    }

    /**
     * Closes the rental when the vehicle is returned.
     */
    public void closeRental() {
        this.status = RentalStatus.CLOSED;
    }
}