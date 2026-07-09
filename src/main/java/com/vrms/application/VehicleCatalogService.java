package com.vrms.application;

import java.util.ArrayList;
import java.util.List;

import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.VehicleRepository;

public class VehicleCatalogService {

    private VehicleRepository vehicleRepository;
    private AuthService authService;

    public VehicleCatalogService(VehicleRepository vehicleRepository, AuthService authService) {
        this.vehicleRepository = vehicleRepository;
        this.authService = authService;
    }

    public List<Vehicle> getAvailableVehicles() {
        if (!authService.isLoggedIn()) {
            throw new IllegalStateException("Please login first");
        }

        List<Vehicle> availableVehicles = new ArrayList<>();

        for (Vehicle vehicle : vehicleRepository.findAll()) {
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
                availableVehicles.add(vehicle);
            }
        }

        return availableVehicles;
    }
}