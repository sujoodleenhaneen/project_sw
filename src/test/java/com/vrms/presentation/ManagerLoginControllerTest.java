package com.vrms.presentation;
import com.vrms.application.AuthService;
import com.vrms.persistence.InMemoryManagerRepository;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ManagerLoginControllerTest {
	private ManagerLoginController controller;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        AuthService authService = new AuthService(new InMemoryManagerRepository());
        controller = new ManagerLoginController(authService);
    }

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
    void loginSuccessfully() {
        String result = controller.login("admin", "1234");

        assertEquals("Login successful", result);
        assertTrue(controller.isLoggedIn());
    }

    @Test
    void loginFails() {
        String result = controller.login("admin", "1111");

        assertEquals("Incorrect username or password", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    void logoutSuccessfully() {
        controller.login("admin", "1234");

        String result = controller.logout();

        assertEquals("Logout successful", result);
        assertFalse(controller.isLoggedIn());
    }

    @Test
    void logoutWithoutLogin() {
        String result = controller.logout();

        assertEquals("No manager is logged in", result);
    }

}
