package com.vrms.domain;

public class Motorcycle extends Vehicle {

    private static final int MINIMUM_RENTAL_AGE = 21;

    public Motorcycle(
            String id,
            String brand,
            String model,
            double pricePerDay,
            VehicleStatus status) {

        super(id, brand, model, pricePerDay, status);
    }

    @Override
    public VehicleType getType() {
        return VehicleType.MOTORCYCLE;
    }

    @Override
    public void validateRental(
            RentalValidationData validationData) {

        super.validateRental(validationData);

        if (validationData.getCustomerAge()
                < MINIMUM_RENTAL_AGE) {

            throw new IllegalArgumentException(
                    "Customer must be at least 21 years old to rent a motorcycle."
            );
        }
    }
}