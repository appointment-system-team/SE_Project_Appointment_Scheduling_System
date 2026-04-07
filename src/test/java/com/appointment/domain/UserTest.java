package com.appointment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void shouldCreateUserWhenDataIsValid() {
        User user = new User("zeina1", "1234", "Zeina Anabtawi", "zeina@gmail.com", "0599123456");

        assertEquals("zeina1", user.getUsername());
        assertEquals("1234", user.getPassword());
        assertEquals("Zeina Anabtawi", user.getFullName());
        assertEquals("zeina@gmail.com", user.getEmail());
        assertEquals("0599123456", user.getPhoneNumber());
    }

    @Test
    void shouldValidatePasswordWhenPasswordIsCorrect() {
        User user = new User("zeina1", "1234", "Zeina Anabtawi", "zeina@gmail.com", "0599123456");

        assertTrue(user.validatePassword("1234"));
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(null, "1234", "Zeina Anabtawi", "zeina@gmail.com", "0599123456"));

        assertEquals("Username cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(" ", "1234", "Zeina Anabtawi", "zeina@gmail.com", "0599123456"));

        assertEquals("Username cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", null, "Zeina Anabtawi", "zeina@gmail.com", "0599123456"));

        assertEquals("Password cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", " ", "Zeina Anabtawi", "zeina@gmail.com", "0599123456"));

        assertEquals("Password cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFullNameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", "1234", null, "zeina@gmail.com", "0599123456"));

        assertEquals("Full name cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFullNameIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", "1234", " ", "zeina@gmail.com", "0599123456"));

        assertEquals("Full name cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", "1234", "Zeina Anabtawi", null, "0599123456"));

        assertEquals("Email cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", "1234", "Zeina Anabtawi", " ", "0599123456"));

        assertEquals("Email cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", "1234", "Zeina Anabtawi", "zeina@gmail.com", null));

        assertEquals("Phone number cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPhoneNumberIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User("zeina1", "1234", "Zeina Anabtawi", "zeina@gmail.com", " "));

        assertEquals("Phone number cannot be empty.", exception.getMessage());
    }
}