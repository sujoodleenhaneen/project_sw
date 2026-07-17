package com.vrms.domain;

public class Vehicle {

    private String id;
    private String brand;
    private String model;
    private double pricePerDay;
    private VehicleStatus status;

    public Vehicle(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        this.id = id;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public VehicleType getType() {
        return VehicleType.CAR;
    }

    public void validateRental(
            RentalValidationData validationData) {

        if (validationData == null) {
            throw new IllegalArgumentException(
                    "Rental validation data cannot be null."
            );
        }
    }

    @Override
    public String toString() {
        return id
                + " - " + getType()
                + " - " + brand
                + " " + model
                + " - " + pricePerDay
                + " per day";
    }
}