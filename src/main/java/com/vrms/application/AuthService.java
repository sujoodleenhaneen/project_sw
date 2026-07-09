package com.vrms.application;

import com.vrms.domain.Manager;
import com.vrms.persistence.ManagerRepository;

public class AuthService {

    private ManagerRepository managerRepository;
    private Manager currentManager;

    public AuthService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public boolean usernameExists(String username) {
        return managerRepository.findByUsername(username) != null;
    }

    public boolean login(String username, String password) {
        Manager manager = managerRepository.findByUsername(username);

        if (manager == null) {
            return false;
        }

        if (!manager.getPassword().equals(password)) {
            return false;
        }

        currentManager = manager;
        return true;
    }

    public void logout() {
        currentManager = null;
    }

    public boolean isLoggedIn() {
        return currentManager != null;
    }

    public Manager getCurrentManager() {
        return currentManager;
    }
}