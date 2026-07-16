package com.vrms.domain;

public class Car extends Vehicle {

    public Car(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        super(id, brand, model, pricePerDay, status);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.CAR;
    }

    @Override
    public void validateRental(
            RentalValidationData validationData) {

        super.validateRental(validationData);
    }
}