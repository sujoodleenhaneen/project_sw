package com.vrms.presentation;

import com.vrms.application.AuthService;

public class ManagerLoginController {

    private AuthService authService;

    public ManagerLoginController(AuthService authService) {
        this.authService = authService;
    }

    public String login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty";
        }

        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty";
        }

        if (!authService.usernameExists(username)) {
            return "Username not found";
        }

        if (!authService.login(username, password)) {
            return "Incorrect password";
        }

        return "Login successful";
    }

    public String logout() {
        if (!authService.isLoggedIn()) {
            return "No manager is logged in";
        }

        authService.logout();
        return "Logout successful";
    }

    public boolean isLoggedIn() {
        return authService.isLoggedIn();
    }
}