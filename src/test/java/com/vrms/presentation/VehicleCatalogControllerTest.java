package com.vrms.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vrms.application.VehicleCatalogService;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;

public class VehicleCatalogControllerTest {

    private VehicleCatalogService vehicleCatalogService;
    private VehicleCatalogController controller;

    @BeforeEach
    public void setUp() {
        vehicleCatalogService = mock(VehicleCatalogService.class);
        controller = new VehicleCatalogController(vehicleCatalogService);
    }

    @Test
    public void viewAvailableVehicles_shouldReturnAvailableVehiclesFromService() {
        List<Vehicle> availableVehicles = Arrays.asList(
                new Vehicle("V1", "Toyota", "Corolla", 40, VehicleStatus.AVAILABLE),
                new Vehicle("V3", "Honda", "Civic", 45, VehicleStatus.AVAILABLE)
        );

        when(vehicleCatalogService.getAvailableVehicles()).thenReturn(availableVehicles);

        List<Vehicle> result = controller.viewAvailableVehicles();

        assertEquals(2, result.size());
        assertEquals("V1", result.get(0).getId());
        assertEquals("Toyota", result.get(0).getBrand());
        assertEquals("V3", result.get(1).getId());
        assertEquals("Honda", result.get(1).getBrand());
    }

    @Test
    public void viewAvailableVehicles_whenNoVehiclesAvailable_shouldReturnEmptyList() {
        when(vehicleCatalogService.getAvailableVehicles()).thenReturn(Arrays.asList());

        List<Vehicle> result = controller.viewAvailableVehicles();

        assertEquals(0, result.size());
    }
}