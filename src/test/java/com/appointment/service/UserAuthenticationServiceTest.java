package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.appointment.domain.User;
import com.appointment.repository.UserRepository;

class UserAuthenticationServiceTest {

    @Test
    void shouldLoginSuccessfullyWithCorrectUsernameAndPassword() {
        UserRepository repository = new UserRepository();
        UserAuthenticationService authenticationService = new UserAuthenticationService(repository);

        boolean result = authenticationService.login("zeina1", "1234");

        assertTrue(result);
        assertTrue(authenticationService.isLoggedIn());
        assertEquals("zeina1", authenticationService.getLoggedUser().getUsername());
    }

    @Test
    void shouldFailLoginWhenUsernameDoesNotExist() {
        UserRepository repository = new UserRepository();
        UserAuthenticationService authenticationService = new UserAuthenticationService(repository);

        boolean result = authenticationService.login("unknown", "1234");

        assertFalse(result);
        assertFalse(authenticationService.isLoggedIn());
        assertNull(authenticationService.getLoggedUser());
    }

    @Test
    void shouldFailLoginWhenPasswordIsIncorrect() {
        UserRepository repository = new UserRepository();
        UserAuthenticationService authenticationService = new UserAuthenticationService(repository);

        boolean result = authenticationService.login("zeina1", "wrong");

        assertFalse(result);
        assertFalse(authenticationService.isLoggedIn());
        assertNull(authenticationService.getLoggedUser());
    }

    @Test
    void shouldLogoutSuccessfully() {
        UserRepository repository = new UserRepository();
        UserAuthenticationService authenticationService = new UserAuthenticationService(repository);

        authenticationService.login("zeina1", "1234");
        authenticationService.logout();

        assertFalse(authenticationService.isLoggedIn());
        assertNull(authenticationService.getLoggedUser());
    }

    @Test
    void shouldReturnFalseWhenNotLoggedInInitially() {
        UserRepository repository = new UserRepository();
        UserAuthenticationService authenticationService = new UserAuthenticationService(repository);

        assertFalse(authenticationService.isLoggedIn());
        assertNull(authenticationService.getLoggedUser());
    }

    @Test
    void shouldReturnLoggedUserAfterSuccessfulLogin() {
        UserRepository repository = new UserRepository();
        UserAuthenticationService authenticationService = new UserAuthenticationService(repository);

        authenticationService.login("ameer1", "1234");

        User loggedUser = authenticationService.getLoggedUser();

        assertTrue(authenticationService.isLoggedIn());
        assertEquals("ameer1", loggedUser.getUsername());
        assertEquals("Ameer Abu Shams", loggedUser.getFullName());
    }
}