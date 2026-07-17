package com.vrms.application;

import java.util.Objects;

import com.vrms.domain.Manager;
import com.vrms.persistence.ManagerRepository;

/**
 * Provides authentication operations for system managers.
 * It supports login, logout, and checking the current login status.
 */
public class AuthService {

    /**
     * Repository used to access manager data.
     */
    private final ManagerRepository managerRepository;

    /**
     * The manager who is currently logged in.
     * It is null when no manager is authenticated.
     */
    private Manager currentManager;

    /**
     * Creates an authentication service.
     *
     * @param managerRepository repository used to access manager data
     * @throws NullPointerException if managerRepository is null
     */
    public AuthService(ManagerRepository managerRepository) {
        this.managerRepository =
                Objects.requireNonNull(managerRepository,
                        "Manager repository cannot be null.");
    }

    /**
     * Checks whether a manager username exists.
     *
     * @param username the username to search for
     * @return true if the username exists, otherwise false
     */
    public boolean usernameExists(String username) {
        return managerRepository.findByUsername(username) != null;
    }

    /**
     * Attempts to authenticate a manager using a username and password.
     *
     * @param username the manager username
     * @param password the manager password
     * @return true if the credentials are valid, otherwise false
     */
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

    /**
     * Logs out the currently authenticated manager.
     */
    public void logout() {
        currentManager = null;
    }

    /**
     * Checks whether a manager is currently logged in.
     *
     * @return true if a manager is logged in, otherwise false
     */
    public boolean isLoggedIn() {
        return currentManager != null;
    }

    /**
     * Returns the currently authenticated manager.
     *
     * @return the current manager, or null if no manager is logged in
     */
    public Manager getCurrentManager() {
        return currentManager;
    }
}