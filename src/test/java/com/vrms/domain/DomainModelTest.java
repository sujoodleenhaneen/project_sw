package com.vrms.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DomainModelTest {

    @Test
    public void vehicleGetters_shouldReturnVehicleData() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40,
                VehicleStatus.AVAILABLE
        );

        assertEquals("V1", vehicle.getId());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(40, vehicle.getPricePerDay());
        assertEquals(VehicleStatus.AVAILABLE, vehicle.getStatus());
    }

    @Test
    public void vehicleSetStatus_shouldChangeVehicleStatus() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40,
                VehicleStatus.AVAILABLE
        );

        vehicle.setStatus(VehicleStatus.RENTED);

        assertEquals(VehicleStatus.RENTED, vehicle.getStatus());
    }

    @Test
    public void vehicleToString_shouldReturnFormattedVehicleText() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40,
                VehicleStatus.AVAILABLE
        );

        assertEquals("V1 - Toyota Corolla - 40.0 per day", vehicle.toString());
    }

    @Test
    public void rentalGetters_shouldReturnRentalData() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40,
                VehicleStatus.AVAILABLE
        );

        Rental rental = new Rental(
                "R1",
                vehicle,
                "Ahmad",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

        assertEquals("R1", rental.getRentalId());
        assertEquals(vehicle, rental.getVehicle());
        assertEquals("Ahmad", rental.getCustomerName());
        assertEquals(LocalDate.of(2026, 7, 10), rental.getStartDate());
        assertEquals(LocalDate.of(2026, 7, 15), rental.getEndDate());
        assertEquals(RentalStatus.ACTIVE, rental.getStatus());
    }

    @Test
    public void closeRental_shouldChangeRentalStatusToClosed() {
        Vehicle vehicle = new Vehicle(
                "V1",
                "Toyota",
                "Corolla",
                40,
                VehicleStatus.AVAILABLE
        );

        Rental rental = new Rental(
                "R1",
                vehicle,
                "Ahmad",
                LocalDate.of(2026, 7, 10),
                LocalDate.of(2026, 7, 15),
                RentalStatus.ACTIVE
        );

        rental.closeRental();

        assertEquals(RentalStatus.CLOSED, rental.getStatus());
    }
}