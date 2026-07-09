package com.vrms.persistence;

import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;

public class InMemoryVehicleRepository implements VehicleRepository {

    private List<Vehicle> vehicles;

    public InMemoryVehicleRepository() {
        vehicles = new ArrayList<>();

        vehicles.add(new Vehicle("V1", "Toyota", "Corolla", 40, VehicleStatus.AVAILABLE));
        vehicles.add(new Vehicle("V2", "Kia", "Sportage", 60, VehicleStatus.RENTED));
        vehicles.add(new Vehicle("V3", "Honda", "Civic", 45, VehicleStatus.AVAILABLE));
        vehicles.add(new Vehicle("V4", "Hyundai", "Tucson", 55, VehicleStatus.RENTED));
    }

    public List<Vehicle> findAll() {
        return vehicles;
    }
}