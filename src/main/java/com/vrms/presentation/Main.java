package com.vrms.presentation;

import java.util.List;
import java.util.Scanner;

import com.vrms.application.AuthService;
import com.vrms.application.VehicleCatalogService;
import com.vrms.domain.Vehicle;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.InMemoryVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.VehicleRepository;

public class Main {

    public static void main(String[] args) {
        ManagerRepository managerRepository = new InMemoryManagerRepository();
        VehicleRepository vehicleRepository = new InMemoryVehicleRepository();

        AuthService authService = new AuthService(managerRepository);
        VehicleCatalogService vehicleCatalogService = new VehicleCatalogService(vehicleRepository, authService);

        ManagerLoginController loginController = new ManagerLoginController(authService);
        VehicleCatalogController vehicleController = new VehicleCatalogController(vehicleCatalogService);

        Scanner input = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println();

            if (!loginController.isLoggedIn()) {
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Choose: ");

                String choice = input.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Username: ");
                        String username = input.nextLine();

                        if (username.trim().isEmpty()) {
                            System.out.println("Username cannot be empty");
                            break;
                        }

                        if (!authService.usernameExists(username)) {
                            System.out.println("Username not found");
                            break;
                        }

                        System.out.print("Password: ");
                        String password = input.nextLine();

                        if (password.trim().isEmpty()) {
                            System.out.println("Password cannot be empty");
                            break;
                        }

                        System.out.println(loginController.login(username, password));
                        break;

                    case "2":
                        run = false;
                        System.out.println("Program closed");
                        break;

                    default:
                        System.out.println("Invalid choice");
                }
            } else {
                System.out.println("1. View available vehicles");
                System.out.println("2. Logout");
                System.out.println("3. Exit");
                System.out.print("Choose: ");

                String choice = input.nextLine();

                switch (choice) {
                    case "1":
                        List<Vehicle> vehicles = vehicleController.viewAvailableVehicles();

                        if (vehicles.isEmpty()) {
                            System.out.println("No available vehicles");
                            break;
                        }

                        System.out.println("Available vehicles:");

                        for (Vehicle vehicle : vehicles) {
                            System.out.println(vehicle);
                        }

                        break;

                    case "2":
                        System.out.println(loginController.logout());
                        break;

                    case "3":
                        run = false;
                        System.out.println("Program closed");
                        break;

                    default:
                        System.out.println("Invalid choice");
                }
            }
        }

        input.close();
    }
}