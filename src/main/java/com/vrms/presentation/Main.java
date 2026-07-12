package com.vrms.presentation;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

import com.vrms.application.AuthService;
import com.vrms.application.EmailNotificationService;
import com.vrms.application.NotificationService;
import com.vrms.application.RentalReminderService;
import com.vrms.application.RentalService;
import com.vrms.application.VehicleCatalogService;
import com.vrms.domain.Rental;
import com.vrms.domain.Vehicle;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.InMemoryRentalRepository;
import com.vrms.persistence.InMemoryVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

/**
 * Runs the console-based Vehicle Rental Management System.
 */
public class Main {

    /**
     * Maximum rental period used to guide console input.
     * RentalService also validates this rule.
     */
    private static final int MAX_RENTAL_DAYS = 30;

    /**
     * Starts the vehicle rental application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        ManagerRepository managerRepository =
                new InMemoryManagerRepository();

        VehicleRepository vehicleRepository =
                new InMemoryVehicleRepository();

        RentalRepository rentalRepository =
                new InMemoryRentalRepository();

        AuthService authService =
                new AuthService(managerRepository);

        VehicleCatalogService vehicleCatalogService =
                new VehicleCatalogService(
                        vehicleRepository,
                        authService
                );

        RentalService rentalService =
                new RentalService(
                        vehicleRepository,
                        rentalRepository
                );

        NotificationService notificationService =
                new EmailNotificationService();

        RentalReminderService rentalReminderService =
                new RentalReminderService(notificationService);

        ManagerLoginController loginController =
                new ManagerLoginController(authService);

        VehicleCatalogController vehicleController =
                new VehicleCatalogController(vehicleCatalogService);

        RentalController rentalController =
                new RentalController(rentalService);

        Scanner input = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println();

            if (!loginController.isLoggedIn()) {

                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Choose: ");

                String choice = input.nextLine().trim();

                switch (choice) {

                    case "1":
                        handleLogin(
                                input,
                                authService,
                                loginController
                        );
                        break;

                    case "2":
                        run = false;
                        System.out.println("Program closed");
                        break;

                    default:
                        System.out.println(
                                "Invalid choice. Please enter 1 or 2."
                        );
                }

            } else {

                System.out.println("1. View available vehicles");
                System.out.println("2. Rent a vehicle");
                System.out.println("3. Logout");
                System.out.println("4. Exit");
                System.out.print("Choose: ");

                String choice = input.nextLine().trim();

                switch (choice) {

                    case "1":
                        displayAvailableVehicles(vehicleController);
                        break;

                    case "2":
                        handleRentalCreation(
                                input,
                                vehicleController,
                                rentalController,
                                rentalReminderService
                        );
                        break;

                    case "3":
                        System.out.println(
                                loginController.logout()
                        );
                        break;

                    case "4":
                        run = false;
                        System.out.println("Program closed");
                        break;

                    default:
                        System.out.println(
                                "Invalid choice. Please enter a number from 1 to 4."
                        );
                }
            }
        }

        input.close();
    }

    /**
     * Handles the manager login process.
     *
     * @param input scanner used to read user input
     * @param authService authentication service
     * @param loginController manager login controller
     */
    private static void handleLogin(
            Scanner input,
            AuthService authService,
            ManagerLoginController loginController) {

        String username;

        while (true) {
            username = readRequiredText(
                    input,
                    "Username: ",
                    "Username cannot be empty."
            );

            if (authService.usernameExists(username)) {
                break;
            }

            System.out.println(
                    "Username not found. Please try again."
            );
        }

        String password = readRequiredText(
                input,
                "Password: ",
                "Password cannot be empty."
        );

        System.out.println(
                loginController.login(username, password)
        );
    }

    /**
     * Displays all currently available vehicles.
     *
     * @param vehicleController vehicle catalog controller
     */
    private static void displayAvailableVehicles(
            VehicleCatalogController vehicleController) {

        List<Vehicle> vehicles =
                vehicleController.viewAvailableVehicles();

        if (vehicles.isEmpty()) {
            System.out.println("No available vehicles");
            return;
        }

        System.out.println("Available vehicles:");

        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle);
        }
    }

    /**
     * Reads rental information and creates a vehicle rental.
     * Invalid input causes only the incorrect field to be requested again.
     *
     * @param input scanner used to read user input
     * @param vehicleController vehicle catalog controller
     * @param rentalController rental controller
     * @param reminderService rental reminder service
     */
    private static void handleRentalCreation(
            Scanner input,
            VehicleCatalogController vehicleController,
            RentalController rentalController,
            RentalReminderService reminderService) {

        List<Vehicle> availableVehicles =
                vehicleController.viewAvailableVehicles();

        if (availableVehicles.isEmpty()) {
            System.out.println(
                    "No available vehicles can be rented."
            );
            return;
        }

        System.out.println("Available vehicles:");

        for (Vehicle vehicle : availableVehicles) {
            System.out.println(vehicle);
        }

        String rentalId = readRequiredText(
                input,
                "Rental ID: ",
                "Rental ID cannot be empty."
        );

        String vehicleId = readAvailableVehicleId(
                input,
                availableVehicles
        );

        String customerName = readRequiredText(
                input,
                "Customer name: ",
                "Customer name cannot be empty."
        );

        String customerEmail = readValidEmail(input);

        LocalDate startDate = readDate(
                input,
                "Start date (YYYY-MM-DD): "
        );

        LocalDate endDate = readValidEndDate(
                input,
                startDate
        );

        try {
            Rental rental = rentalController.rentVehicle(
                    rentalId,
                    vehicleId,
                    customerName,
                    customerEmail,
                    startDate,
                    endDate
            );

            System.out.println();
            System.out.println(
                    "Rental created successfully."
            );
            System.out.println(
                    "Rental ID: " + rental.getRentalId()
            );
            System.out.println(
                    "Customer: " + rental.getCustomerName()
            );
            System.out.println(
                    "Customer email: "
                            + rental.getCustomerEmail()
            );
            System.out.println(
                    "Vehicle: " + rental.getVehicle()
            );
            System.out.println(
                    "Start date: " + rental.getStartDate()
            );
            System.out.println(
                    "End date: " + rental.getEndDate()
            );

            boolean reminderGenerated =
                    reminderService.checkAndSendReminder(
                            rental,
                            LocalDate.now()
                    );

            if (!reminderGenerated) {
                System.out.println(
                        "No expiry reminder is required today."
                );
            }

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            System.out.println(
                    "Rental failed: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Reads text that must not be empty.
     *
     * @param input scanner used to read user input
     * @param prompt message displayed to the user
     * @param errorMessage message displayed for empty input
     * @return non-empty trimmed text
     */
    private static String readRequiredText(
            Scanner input,
            String prompt,
            String errorMessage) {

        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println(errorMessage);
        }
    }

    /**
     * Reads an available vehicle identifier.
     *
     * @param input scanner used to read user input
     * @param availableVehicles list of available vehicles
     * @return identifier of an available vehicle
     */
    private static String readAvailableVehicleId(
            Scanner input,
            List<Vehicle> availableVehicles) {

        while (true) {
            String vehicleId = readRequiredText(
                    input,
                    "Vehicle ID: ",
                    "Vehicle ID cannot be empty."
            );

            for (Vehicle vehicle : availableVehicles) {
                if (vehicle.getId().equalsIgnoreCase(vehicleId)) {
                    return vehicle.getId();
                }
            }

            System.out.println(
                    "Invalid or unavailable vehicle ID. "
                            + "Please choose an ID from the displayed list."
            );
        }
    }

    /**
     * Reads a valid customer email address.
     *
     * @param input scanner used to read user input
     * @return valid email address
     */
    private static String readValidEmail(Scanner input) {

        while (true) {
            String email = readRequiredText(
                    input,
                    "Customer email: ",
                    "Customer email cannot be empty."
            );

            if (email.matches(
                    "^[A-Za-z0-9+_.-]+"
                            + "@[A-Za-z0-9.-]+"
                            + "\\.[A-Za-z]{2,}$")) {

                return email;
            }

            System.out.println(
                    "Invalid email. Example: haneen@example.com"
            );
        }
    }

    /**
     * Reads a valid date using the ISO format YYYY-MM-DD.
     *
     * @param input scanner used to read user input
     * @param prompt message displayed to the user
     * @return valid date
     */
    private static LocalDate readDate(
            Scanner input,
            String prompt) {

        while (true) {
            System.out.print(prompt);
            String dateText = input.nextLine().trim();

            try {
                return LocalDate.parse(dateText);

            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Invalid date. Use YYYY-MM-DD, "
                                + "for example: 2026-07-13."
                );
            }
        }
    }

    /**
     * Reads a valid rental end date.
     * The date must be after the start date and within 30 days.
     *
     * @param input scanner used to read user input
     * @param startDate rental start date
     * @return valid rental end date
     */
    private static LocalDate readValidEndDate(
            Scanner input,
            LocalDate startDate) {

        while (true) {
            LocalDate endDate = readDate(
                    input,
                    "End date (YYYY-MM-DD): "
            );

            if (!endDate.isAfter(startDate)) {
                System.out.println(
                        "End date must be after the start date."
                );
                continue;
            }

            long rentalDays = ChronoUnit.DAYS.between(
                    startDate,
                    endDate
            );

            if (rentalDays > MAX_RENTAL_DAYS) {
                System.out.println(
                        "Rental period cannot exceed "
                                + MAX_RENTAL_DAYS
                                + " days."
                );
                continue;
            }

            return endDate;
        }
    }
}