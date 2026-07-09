package com.vrms.application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleStatus;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.InMemoryVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.VehicleRepository;
import java.util.List;
class VehicleCatalogServiceTest {
	private AuthService authService;
    private VehicleCatalogService vehicleCatalogService;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        ManagerRepository managerRepository = new InMemoryManagerRepository();
        VehicleRepository vehicleRepository = new InMemoryVehicleRepository();

        authService = new AuthService(managerRepository);
        vehicleCatalogService = new VehicleCatalogService(vehicleRepository, authService);
    }

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
    void displayAvailableVehicles() {
        authService.login("admin", "1234");

        List<Vehicle> vehicles = vehicleCatalogService.getAvailableVehicles();

        assertEquals(2, vehicles.size());
        assertTrue(vehicles.get(0).getStatus() == VehicleStatus.AVAILABLE);
        assertTrue(vehicles.get(1).getStatus() == VehicleStatus.AVAILABLE);
    }

    @Test
    void hideRentedVehicles() {
        authService.login("admin", "1234");

        List<Vehicle> vehicles = vehicleCatalogService.getAvailableVehicles();

        for (Vehicle vehicle : vehicles) {
            assertTrue(vehicle.getStatus() != VehicleStatus.RENTED);
        }
    }

    @Test
    void requireLoginToViewVehicles() {
        assertThrows(IllegalStateException.class, () -> vehicleCatalogService.getAvailableVehicles());
    }

}
