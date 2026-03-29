package com.appointment.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.appointment.domain.AppointmentMode;

class AppointmentModeRuleFactoryTest {

    @Test
    void testGetVirtualRule() {
        AppointmentModeRuleFactory factory = new AppointmentModeRuleFactory();
        AppointmentModeRule rule = factory.getRule(AppointmentMode.VIRTUAL);

        assertTrue(rule instanceof VirtualAppointmentRule);
    }

    @Test
    void testGetInPersonRule() {
        AppointmentModeRuleFactory factory = new AppointmentModeRuleFactory();
        AppointmentModeRule rule = factory.getRule(AppointmentMode.IN_PERSON);

        assertTrue(rule instanceof InPersonAppointmentRule);
    }

    @Test
    void testNullModeThrowsException() {
        AppointmentModeRuleFactory factory = new AppointmentModeRuleFactory();

        assertThrows(IllegalArgumentException.class, () -> factory.getRule(null));
    }
}