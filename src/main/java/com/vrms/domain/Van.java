package com.vrms.domain;

public class Van extends Vehicle {

    public Van(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        super(id, brand, model, pricePerDay, status);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.VAN;
    }

    @Override
    public void validateRental(
            RentalValidationData validationData) {

        super.validateRental(validationData);
    }
}