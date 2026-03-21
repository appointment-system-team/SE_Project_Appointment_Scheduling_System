package com.appointment.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AdministratorTest {

    @Test
    void shouldReturnUsernameCorrectly() {
        Administrator administrator = new Administrator("admin", "1234");

        assertEquals("admin", administrator.getUsername());
    }

    @Test
    void shouldValidatePasswordWhenCorrect() {
        Administrator administrator = new Administrator("admin", "1234");

        assertTrue(administrator.validatePassword("1234"));
    }

    @Test
    void shouldReturnFalseWhenPasswordIsIncorrect() {
        Administrator administrator = new Administrator("admin", "1234");

        assertFalse(administrator.validatePassword("wrong"));
    }
}