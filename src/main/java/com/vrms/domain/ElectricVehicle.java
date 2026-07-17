package com.vrms.domain;

public class ElectricVehicle extends Vehicle {

    public ElectricVehicle(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        super(id, brand, model, pricePerDay, status);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.ELECTRIC_VEHICLE;
    }

    @Override
    public void validateRental(
            RentalValidationData validationData) {

        super.validateRental(validationData);

        if (!validationData.isBatteryChecked()) {
            throw new IllegalArgumentException(
                    "Battery check is required before renting an electric vehicle."
            );
        }
    }
}