package com.vrms.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vrms.persistence.FileManagerRepository;
import com.vrms.persistence.ManagerRepository;

class AuthServiceTest {

    @TempDir
    Path tempDir;

    private AuthService authService;

    @BeforeEach
    void setUp() throws IOException {
        Path managersFile = tempDir.resolve("managers.txt");
        Files.write(managersFile, Arrays.asList("admin,1234"), StandardCharsets.UTF_8);

        ManagerRepository managerRepository = new FileManagerRepository(managersFile);
        authService = new AuthService(managerRepository);
    }

    @Test
    void loginWithCorrectData() {
        boolean result = authService.login("admin", "1234");

        assertTrue(result);
        assertTrue(authService.isLoggedIn());
    }

    @Test
    void loginWithWrongPassword() {
        boolean result = authService.login("admin", "1111");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void loginWithWrongUsername() {
        boolean result = authService.login("user", "1234");

        assertFalse(result);
        assertFalse(authService.isLoggedIn());
    }

    @Test
    void logoutManager() {
        authService.login("admin", "1234");
        authService.logout();

        assertFalse(authService.isLoggedIn());
    }
}