package com.vrms.presentation;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.vrms.application.AuthService;
import com.vrms.application.EmailNotificationService;
import com.vrms.application.NotificationService;
import com.vrms.application.RentalReminderService;
import com.vrms.application.RentalService;
import com.vrms.application.VehicleCatalogService;
import com.vrms.domain.Rental;
import com.vrms.domain.RentalValidationData;
import com.vrms.domain.StandardStrategy;
import com.vrms.domain.Vehicle;
import com.vrms.domain.VehicleType;
import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.FileRentalRepository;
import com.vrms.persistence.FileVehicleRepository;
import com.vrms.persistence.ManagerRepository;
import com.vrms.persistence.RentalRepository;
import com.vrms.persistence.VehicleRepository;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String RENTAL_ID_LABEL = "Rental ID: ";
    private static final int MAX_RENTAL_DAYS = 30;

    public static void main(String[] args) {
        ManagerRepository managerRepository = new FileManagerRepository(Paths.get("data", "managers.txt"));
        VehicleRepository vehicleRepository = new FileVehicleRepository(Paths.get("data", "vehicles.txt"));
        RentalRepository rentalRepository = new FileRentalRepository(Paths.get("data", "rentals.txt"), vehicleRepository);

        AuthService authService = new AuthService(managerRepository);
        VehicleCatalogService vehicleCatalogService = new VehicleCatalogService(vehicleRepository, authService);
        RentalService rentalService = new RentalService(vehicleRepository, rentalRepository);
        rentalService.setRentalStrategy(new StandardStrategy());

        NotificationService notificationService = new EmailNotificationService();
        RentalReminderService rentalReminderService = new RentalReminderService(notificationService, rentalRepository);

        ManagerLoginController loginController = new ManagerLoginController(authService);
        VehicleCatalogController vehicleController = new VehicleCatalogController(vehicleCatalogService);
        RentalController rentalController = new RentalController(rentalService);

        Scanner input = new Scanner(System.in);
        boolean run = true;
        System.out.println("Test SonarQube Smell");

        while (run) {
            println();

            if (!loginController.isLoggedIn()) {
            	 System.out.println("1. Login");
                println("2. Exit");
                print("Choose: ");

                String choice = input.nextLine().trim();

                switch (choice) {
                    case "1":
                        handleLogin(input, authService, loginController);
                        break;
                    case "2":
                        run = false;
                        println("Program closed");
                        break;
                    default:
                        println("Invalid choice. Please enter 1 or 2.");
                }
            } else {
                println("1. View available vehicles");
                println("2. Rent a vehicle");
                println("3. Check rental expiry reminders");
                println("4. Return vehicle");
                println("5. Logout");
                println("6. Exit");
                print("Choose: ");

                String choice = input.nextLine().trim();

                switch (choice) {
                    case "1":
                        displayAvailableVehicles(vehicleController);
                        break;
                    case "2":
                        handleRentalCreation(input, vehicleController, rentalController, notificationService);
                        break;
                    case "3":
                        handleReminderCheck(rentalReminderService);
                        break;
                    case "4":
                        handleVehicleReturn(input, rentalController);
                        break;
                    case "5":
                        println(loginController.logout());
                        break;
                    case "6":
                        run = false;
                        println("Program closed");
                        break;
                    default:
                        println("Invalid choice. Please enter a number from 1 to 6.");
                }
            }
        }

        input.close();
    }

    private static void handleLogin(Scanner input, AuthService authService, ManagerLoginController loginController) {
        String username;

        while (true) {
            username = readRequiredText(input, "Username: ", "Username cannot be empty.");

            if (authService.usernameExists(username)) {
                break;
            }

            println("Username not found. Please try again.");
        }

        String password = readRequiredText(input, "Password: ", "Password cannot be empty.");
        println(loginController.login(username, password));
    }

    private static void displayAvailableVehicles(VehicleCatalogController vehicleController) {
        List<Vehicle> vehicles = vehicleController.viewAvailableVehicles();

        if (vehicles.isEmpty()) {
            println("No available vehicles");
            return;
        }

        println("Available vehicles:");

        for (Vehicle vehicle : vehicles) {
            println(vehicle);
        }
    }

    private static void handleRentalCreation(Scanner input, VehicleCatalogController vehicleController,
            RentalController rentalController, NotificationService notificationService) {

        List<Vehicle> availableVehicles = vehicleController.viewAvailableVehicles();

        if (availableVehicles.isEmpty()) {
            println("No available vehicles can be rented.");
            return;
        }

        println("Available vehicles:");

        for (Vehicle vehicle : availableVehicles) {
            println(vehicle);
        }

        String rentalId = readRequiredText(input, RENTAL_ID_LABEL, "Rental ID cannot be empty.");
        String vehicleId = readAvailableVehicleId(input, availableVehicles);
        Vehicle selectedVehicle = findVehicleById(availableVehicles, vehicleId);

        String customerName = readRequiredText(input, "Customer name: ", "Customer name cannot be empty.");
        String customerEmail = readValidEmail(input);
        RentalValidationData validationData = readValidationData(input, selectedVehicle);
        LocalDate startDate = readDate(input, "Start date (YYYY-MM-DD): ");
        LocalDate endDate = readValidEndDate(input, startDate);

        try {
            Rental rental = rentalController.rentVehicle(
                    rentalId,
                    vehicleId,
                    customerName,
                    customerEmail,
                    startDate,
                    endDate,
                    validationData
            );

            println();
            println("Rental created successfully.");
            println(RENTAL_ID_LABEL + rental.getRentalId());
            println("Customer: " + rental.getCustomerName());
            println("Customer email: " + rental.getCustomerEmail());
            println("Vehicle: " + rental.getVehicle());
            println("Vehicle type: " + rental.getVehicle().getType());
            println("Start date: " + rental.getStartDate());
            println("End date: " + rental.getEndDate());

            try {
                notificationService.sendNotification(
                        rental.getCustomerEmail(),
                        "Rental Accepted",
                        "Hello " + rental.getCustomerName()
                                + ", your rental for "
                                + rental.getVehicle().getBrand()
                                + " "
                                + rental.getVehicle().getModel()
                                + " has been accepted."
                );
            } catch (IllegalStateException exception) {
                println("Rental created, but email was not sent: " + exception.getMessage());
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            println("Rental failed: " + exception.getMessage());
        }
    }

    private static RentalValidationData readValidationData(Scanner input, Vehicle vehicle) {
        int customerAge = 0;
        boolean specialTruckLicense = false;
        boolean batteryChecked = false;

        if (vehicle.getType() == VehicleType.MOTORCYCLE) {
            customerAge = readNonNegativeInteger(input, "Customer age: ");
        }

        if (vehicle.getType() == VehicleType.TRUCK) {
            specialTruckLicense = readYesOrNo(
                    input,
                    "Does the customer have a special truck license? (yes/no): "
            );
        }

        if (vehicle.getType() == VehicleType.ELECTRIC_VEHICLE) {
            batteryChecked = readYesOrNo(
                    input,
                    "Was the vehicle battery checked? (yes/no): "
            );
        }

        return new RentalValidationData(customerAge, specialTruckLicense, batteryChecked);
    }

    private static Vehicle findVehicleById(List<Vehicle> vehicles, String vehicleId) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equalsIgnoreCase(vehicleId)) {
                return vehicle;
            }
        }

        throw new IllegalArgumentException("Vehicle not found.");
    }

    private static void handleVehicleReturn(Scanner input, RentalController rentalController) {
        String vehicleId = readRequiredText(
                input,
                "Vehicle ID to return: ",
                "Vehicle ID cannot be empty."
        );

        try {
            Rental rental = rentalController.returnVehicle(vehicleId);

            println();
            println("Vehicle returned successfully.");
            println(RENTAL_ID_LABEL + rental.getRentalId());
            println("Customer: " + rental.getCustomerName());
            println("Vehicle: " + rental.getVehicle());
            println("Rental status: " + rental.getStatus());
            printf("Total rental cost: %.2f%n", rental.getTotalCost());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            println("Vehicle return failed: " + exception.getMessage());
        }
    }

    private static void handleReminderCheck(RentalReminderService reminderService) {
        try {
            int remindersGenerated = reminderService.checkAllRentalsAndSendReminders(LocalDate.now());
            println("Reminders generated: " + remindersGenerated);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            println("Reminder check failed: " + exception.getMessage());
        }
    }

    private static String readRequiredText(Scanner input, String prompt, String errorMessage) {
        while (true) {
            print(prompt);
            String value = input.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            println(errorMessage);
        }
    }

    private static String readAvailableVehicleId(Scanner input, List<Vehicle> availableVehicles) {
        while (true) {
            String vehicleId = readRequiredText(input, "Vehicle ID: ", "Vehicle ID cannot be empty.");

            for (Vehicle vehicle : availableVehicles) {
                if (vehicle.getId().equalsIgnoreCase(vehicleId)) {
                    return vehicle.getId();
                }
            }

            println("Invalid or unavailable vehicle ID. Please choose an ID from the displayed list.");
        }
    }

    private static String readValidEmail(Scanner input) {
        while (true) {
            String email = readRequiredText(input, "Customer email: ", "Customer email cannot be empty.");

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }

            println("Invalid email. Example: haneen@example.com");
        }
    }

    private static LocalDate readDate(Scanner input, String prompt) {
        while (true) {
            print(prompt);
            String dateText = input.nextLine().trim();

            try {
                return LocalDate.parse(dateText);
            } catch (DateTimeParseException exception) {
                println("Invalid date. Use YYYY-MM-DD, for example: 2026-07-13.");
            }
        }
    }

    private static LocalDate readValidEndDate(Scanner input, LocalDate startDate) {
        while (true) {
            LocalDate endDate = readDate(input, "End date (YYYY-MM-DD): ");

            if (!endDate.isAfter(startDate)) {
                println("End date must be after the start date.");
                continue;
            }

            long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);

            if (rentalDays > MAX_RENTAL_DAYS) {
                println("Rental period cannot exceed " + MAX_RENTAL_DAYS + " days.");
                continue;
            }

            return endDate;
        }
    }

    private static int readNonNegativeInteger(Scanner input, String prompt) {
        while (true) {
            print(prompt);
            String value = input.nextLine().trim();

            try {
                int number = Integer.parseInt(value);

                if (number >= 0) {
                    return number;
                }
            } catch (NumberFormatException exception) {
            }

            println("Please enter a valid non-negative number.");
        }
    }

    private static boolean readYesOrNo(Scanner input, String prompt) {
        while (true) {
            print(prompt);
            String answer = input.nextLine().trim();

            if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
                return true;
            }

            if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
                return false;
            }

            println("Please enter yes or no.");
        }
    }

    private static void print(String message) {
        LOGGER.info(message);
    }

    private static void println() {
        LOGGER.info("");
    }

    private static void println(String message) {
        LOGGER.info(message);
    }

    private static void println(Object object) {
        LOGGER.info(String.valueOf(object));
    }

    private static void printf(String format, Object... args) {
        LOGGER.info(() -> String.format(format, args));
    }
}