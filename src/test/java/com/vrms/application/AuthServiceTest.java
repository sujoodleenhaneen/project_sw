
package com.vrms.application;
import com.vrms.persistence.InMemoryManagerRepository;
import com.vrms.persistence.ManagerRepository;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthServiceTest {
	private AuthService authService;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
    void setUp() {
        ManagerRepository managerRepository = new InMemoryManagerRepository();
        authService = new AuthService(managerRepository);
    }

	@AfterEach
	void tearDown() throws Exception {
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
