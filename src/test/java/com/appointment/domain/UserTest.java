package com.appointment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void shouldCreateUserWhenNameIsValid() {
        User user = new User("Zeina");

        assertEquals("Zeina", user.getName());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(null));

        assertEquals("User name cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(" "));

        assertEquals("User name cannot be empty.", exception.getMessage());
    }
}