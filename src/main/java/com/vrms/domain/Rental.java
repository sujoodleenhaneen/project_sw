package com.vrms.domain;

import java.time.LocalDate;

/**
 * Represents a rental record for a vehicle in the Vehicle Rental Management System.
 */
public class Rental {

    private String rentalId;
    private Vehicle vehicle;
    private String customerName;
    private String customerEmail;
    private LocalDate startDate;
    private LocalDate endDate;
    private RentalStatus status;
    private double totalCost;

    /**
     * Creates a new rental with an initial total cost of zero.
     *
     * @param rentalId unique identifier for the rental
     * @param vehicle rented vehicle
     * @param customerName customer name
     * @param customerEmail customer email
     * @param startDate rental start date
     * @param endDate expected rental end date
     * @param status rental status
     */
    public Rental(String rentalId, Vehicle vehicle, String customerName,
                  String customerEmail, LocalDate startDate,
                  LocalDate endDate, RentalStatus status) {

        this(
                rentalId,
                vehicle,
                customerName,
                customerEmail,
                startDate,
                endDate,
                status,
                0.0
        );
    }

    /**
     * Creates a rental and restores its saved total cost.
     *
     * @param rentalId unique identifier for the rental
     * @param vehicle rented vehicle
     * @param customerName customer name
     * @param customerEmail customer email
     * @param startDate rental start date
     * @param endDate expected rental end date
     * @param status rental status
     * @param totalCost saved total rental cost
     */
    public Rental(String rentalId, Vehicle vehicle, String customerName,
                  String customerEmail, LocalDate startDate,
                  LocalDate endDate, RentalStatus status,
                  double totalCost) {

        this.rentalId = rentalId;
        this.vehicle = vehicle;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.totalCost = totalCost;
    }

    public String getRentalId() {
        return rentalId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public void closeRental() {
        this.status = RentalStatus.CLOSED;
    }
}