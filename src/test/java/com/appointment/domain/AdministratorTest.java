package com.appointment.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AdministratorTest {

    @Test
    void shouldReturnAdminDataCorrectly() {
        Administrator administrator = new Administrator(
                "admin",
                "1234",
                "System Admin",
                "admin@appointment.com",
                "0599000000"
        );

        assertEquals("admin", administrator.getUsername());
        assertEquals("1234", administrator.getPassword());
        assertEquals("System Admin", administrator.getFullName());
        assertEquals("admin@appointment.com", administrator.getEmail());
        assertEquals("0599000000", administrator.getPhoneNumber());
    }

    @Test
    void shouldValidatePasswordWhenCorrect() {
        Administrator administrator = new Administrator(
                "admin",
                "1234",
                "System Admin",
                "admin@appointment.com",
                "0599000000"
        );

        assertTrue(administrator.validatePassword("1234"));
    }

    @Test
    void shouldReturnFalseWhenPasswordIsIncorrect() {
        Administrator administrator = new Administrator(
                "admin",
                "1234",
                "System Admin",
                "admin@appointment.com",
                "0599000000"
        );

        assertFalse(administrator.validatePassword("wrong"));
    }

    @Test
    void shouldThrowWhenUsernameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        null,
                        "1234",
                        "System Admin",
                        "admin@appointment.com",
                        "0599000000"
                )
        );

        assertEquals("Username cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "   ",
                        "1234",
                        "System Admin",
                        "admin@appointment.com",
                        "0599000000"
                )
        );

        assertEquals("Username cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        null,
                        "System Admin",
                        "admin@appointment.com",
                        "0599000000"
                )
        );

        assertEquals("Password cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPasswordIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        "   ",
                        "System Admin",
                        "admin@appointment.com",
                        "0599000000"
                )
        );

        assertEquals("Password cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenFullNameIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        "1234",
                        null,
                        "admin@appointment.com",
                        "0599000000"
                )
        );

        assertEquals("Full name cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenFullNameIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        "1234",
                        "   ",
                        "admin@appointment.com",
                        "0599000000"
                )
        );

        assertEquals("Full name cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenEmailIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        "1234",
                        "System Admin",
                        null,
                        "0599000000"
                )
        );

        assertEquals("Email cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenEmailIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        "1234",
                        "System Admin",
                        "   ",
                        "0599000000"
                )
        );

        assertEquals("Email cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPhoneNumberIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        "1234",
                        "System Admin",
                        "admin@appointment.com",
                        null
                )
        );

        assertEquals("Phone number cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowWhenPhoneNumberIsBlank() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Administrator(
                        "admin",
                        "1234",
                        "System Admin",
                        "admin@appointment.com",
                        "   "
                )
        );

        assertEquals("Phone number cannot be empty.", exception.getMessage());
    }
}