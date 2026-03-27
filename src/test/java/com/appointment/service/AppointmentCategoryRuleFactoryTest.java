package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.appointment.domain.AppointmentCategory;

class AppointmentCategoryRuleFactoryTest {

    @Test
    void shouldReturnIndividualAppointmentRule() {
        AppointmentCategoryRuleFactory factory = new AppointmentCategoryRuleFactory();

        AppointmentCategoryRule rule = factory.getRule(AppointmentCategory.INDIVIDUAL);

        assertInstanceOf(IndividualAppointmentRule.class, rule);
    }

    @Test
    void shouldReturnGroupAppointmentRule() {
        AppointmentCategoryRuleFactory factory = new AppointmentCategoryRuleFactory();

        AppointmentCategoryRule rule = factory.getRule(AppointmentCategory.GROUP);

        assertInstanceOf(GroupAppointmentRule.class, rule);
    }

    @Test
    void shouldThrowExceptionWhenAppointmentCategoryIsNull() {
        AppointmentCategoryRuleFactory factory = new AppointmentCategoryRuleFactory();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.getRule(null));

        assertEquals("Appointment category cannot be null.", exception.getMessage());
    }
}