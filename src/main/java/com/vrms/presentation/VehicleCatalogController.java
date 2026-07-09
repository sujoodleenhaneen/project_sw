package com.vrms.presentation;

import java.util.List;

import com.vrms.application.VehicleCatalogService;
import com.vrms.domain.Vehicle;

public class VehicleCatalogController {

    private VehicleCatalogService vehicleCatalogService;

    public VehicleCatalogController(VehicleCatalogService vehicleCatalogService) {
        this.vehicleCatalogService = vehicleCatalogService;
    }

    public List<Vehicle> viewAvailableVehicles() {
        return vehicleCatalogService.getAvailableVehicles();
    }
}