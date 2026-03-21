package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.appointment.repository.AdminRepository;

class AuthenticationServiceTest {

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        AuthenticationService service = new AuthenticationService(new AdminRepository());

        boolean result = service.login("admin", "1234");

        assertTrue(result);
        assertTrue(service.isLoggedIn());
    }

    @Test
    void shouldFailLoginWithInvalidUsername() {
        AuthenticationService service = new AuthenticationService(new AdminRepository());

        boolean result = service.login("unknown", "1234");

        assertFalse(result);
        assertFalse(service.isLoggedIn());
    }

    @Test
    void shouldFailLoginWithInvalidPassword() {
        AuthenticationService service = new AuthenticationService(new AdminRepository());

        boolean result = service.login("admin", "wrong");

        assertFalse(result);
        assertFalse(service.isLoggedIn());
    }

    @Test
    void shouldLogoutSuccessfully() {
        AuthenticationService service = new AuthenticationService(new AdminRepository());

        service.login("admin", "1234");
        service.logout();

        assertFalse(service.isLoggedIn());
    }
}