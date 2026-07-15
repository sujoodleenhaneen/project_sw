package com.vrms.domain;

public class RentalValidationData {

    private final int customerAge;
    private final boolean specialTruckLicense;
    private final boolean batteryChecked;

    public RentalValidationData(
            int customerAge,
            boolean specialTruckLicense,
            boolean batteryChecked) {

        if (customerAge < 0) {
            throw new IllegalArgumentException(
                    "Customer age cannot be negative."
            );
        }

        this.customerAge = customerAge;
        this.specialTruckLicense = specialTruckLicense;
        this.batteryChecked = batteryChecked;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public boolean hasSpecialTruckLicense() {
        return specialTruckLicense;
    }

    public boolean isBatteryChecked() {
        return batteryChecked;
    }
}