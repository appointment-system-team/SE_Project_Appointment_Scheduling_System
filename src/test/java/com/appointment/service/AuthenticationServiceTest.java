package com.appointment.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appointment.repository.AdminRepository;

class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(new AdminRepository());
    }

    @Test
    void testLoginSuccess() {
        boolean result = authenticationService.login("admin", "1234");

        assertTrue(result);
        assertTrue(authenticationService.isLoggedIn());
        assertNotNull(authenticationService.getLoggedAdmin());
    }

    @Test
    void testLoginFailure() {
        boolean result = authenticationService.login("wrong", "wrong");

        assertFalse(result);
        assertFalse(authenticationService.isLoggedIn());
        assertNull(authenticationService.getLoggedAdmin());
    }

    @Test
    void testLogout() {
        authenticationService.login("admin", "1234");
        authenticationService.logout();

        assertFalse(authenticationService.isLoggedIn());
        assertNull(authenticationService.getLoggedAdmin());
    }
}