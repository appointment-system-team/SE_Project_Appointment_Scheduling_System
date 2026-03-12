package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParticipantLimitRuleTest {

    @Test
    void shouldReturnTrueWhenParticipantCountIsWithinAllowedRange() {
        ParticipantLimitRule rule = new ParticipantLimitRule();

        assertTrue(rule.isValid(1));
        assertTrue(rule.isValid(3));
        assertTrue(rule.isValid(5));
    }

    @Test
    void shouldReturnFalseWhenParticipantCountIsInvalid() {
        ParticipantLimitRule rule = new ParticipantLimitRule();

        assertFalse(rule.isValid(0));
        assertFalse(rule.isValid(-2));
        assertFalse(rule.isValid(6));
    }

    @Test
    void shouldReturnConfiguredMaximumParticipants() {
        ParticipantLimitRule rule = new ParticipantLimitRule();

        assertEquals(5, rule.getMaxParticipants());
    }
}