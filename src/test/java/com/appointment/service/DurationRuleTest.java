package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DurationRuleTest {

    @Test
    void shouldReturnTrueWhenDurationIsWithinAllowedRange() {
        DurationRule rule = new DurationRule();

        assertTrue(rule.isValid(1));
        assertTrue(rule.isValid(60));
        assertTrue(rule.isValid(120));
    }

    @Test
    void shouldReturnFalseWhenDurationIsInvalid() {
        DurationRule rule = new DurationRule();

        assertFalse(rule.isValid(0));
        assertFalse(rule.isValid(-10));
        assertFalse(rule.isValid(121));
    }

    @Test
    void shouldReturnConfiguredMaximumDuration() {
        DurationRule rule = new DurationRule();

        assertEquals(120, rule.getMaxDurationInMinutes());
    }
}