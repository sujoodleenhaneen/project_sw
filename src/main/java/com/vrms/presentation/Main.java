package com.vrms.presentation;

import java.util.Scanner;

import com.vrms.application.AuthService;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.ManagerRepository;

public class Main {

    public static void main(String[] args) {
        ManagerRepository managerRepository = new InMemoryManagerRepository();
        AuthService authService = new AuthService(managerRepository);
        ManagerLoginController controller = new ManagerLoginController(authService);

        Scanner input = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println();

            if (!controller.isLoggedIn()) {
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

                        System.out.println(controller.login(username, password));
                        break;

                    case "2":
                        run = false;
                        System.out.println("Program closed");
                        break;

                    default:
                        System.out.println("Invalid choice");
                }
            } else {
                System.out.println("1. Open protected page");
                System.out.println("2. Logout");
                System.out.println("3. Exit");
                System.out.print("Choose: ");

                String choice = input.nextLine();

                switch (choice) {
                    case "1":
                        System.out.println("Protected page opened");
                        break;

                    case "2":
                        System.out.println(controller.logout());
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