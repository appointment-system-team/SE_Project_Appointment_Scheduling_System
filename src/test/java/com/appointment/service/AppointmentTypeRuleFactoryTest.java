package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.appointment.domain.AppointmentType;

class AppointmentTypeRuleFactoryTest {

    @Test
    void shouldReturnUrgentAppointmentRule() {
        AppointmentTypeRuleFactory factory = new AppointmentTypeRuleFactory();

        AppointmentTypeRule rule = factory.getRule(AppointmentType.URGENT);

        assertInstanceOf(UrgentAppointmentRule.class, rule);
    }

    @Test
    void shouldReturnFollowUpAppointmentRule() {
        AppointmentTypeRuleFactory factory = new AppointmentTypeRuleFactory();

        AppointmentTypeRule rule = factory.getRule(AppointmentType.FOLLOW_UP);

        assertInstanceOf(FollowUpAppointmentRule.class, rule);
    }

    @Test
    void shouldReturnAssessmentAppointmentRule() {
        AppointmentTypeRuleFactory factory = new AppointmentTypeRuleFactory();

        AppointmentTypeRule rule = factory.getRule(AppointmentType.ASSESSMENT);

        assertInstanceOf(AssessmentAppointmentRule.class, rule);
    }

    @Test
    void shouldThrowExceptionWhenAppointmentTypeIsNull() {
        AppointmentTypeRuleFactory factory = new AppointmentTypeRuleFactory();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.getRule(null));

        assertEquals("Appointment type cannot be null.", exception.getMessage());
    }
}