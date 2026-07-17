package com.vrms.domain;

public class Truck extends Vehicle {

    public Truck(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        super(id, brand, model, pricePerDay, status);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.TRUCK;
    }

    @Override
    public void validateRental(
            RentalValidationData validationData) {

        super.validateRental(validationData);

        if (!validationData.hasSpecialTruckLicense()) {
            throw new IllegalArgumentException(
                    "A special truck license is required."
            );
        }
    }
}