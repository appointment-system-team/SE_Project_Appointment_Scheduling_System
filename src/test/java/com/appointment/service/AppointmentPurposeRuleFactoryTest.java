package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.appointment.domain.AppointmentPurpose;

class AppointmentPurposeRuleFactoryTest {

    @Test
    void shouldReturnUrgentAppointmentRule() {
        AppointmentPurposeRuleFactory factory = new AppointmentPurposeRuleFactory();

        AppointmentPurposeRule rule = factory.getRule(AppointmentPurpose.URGENT);

        assertInstanceOf(UrgentAppointmentRule.class, rule);
    }

    @Test
    void shouldReturnFollowUpAppointmentRule() {
        AppointmentPurposeRuleFactory factory = new AppointmentPurposeRuleFactory();

        AppointmentPurposeRule rule = factory.getRule(AppointmentPurpose.FOLLOW_UP);

        assertInstanceOf(FollowUpAppointmentRule.class, rule);
    }

    @Test
    void shouldReturnAssessmentAppointmentRule() {
        AppointmentPurposeRuleFactory factory = new AppointmentPurposeRuleFactory();

        AppointmentPurposeRule rule = factory.getRule(AppointmentPurpose.ASSESSMENT);

        assertInstanceOf(AssessmentAppointmentRule.class, rule);
    }

    @Test
    void shouldThrowExceptionWhenAppointmentPurposeIsNull() {
        AppointmentPurposeRuleFactory factory = new AppointmentPurposeRuleFactory();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.getRule(null));

        assertEquals("Appointment purpose cannot be null.", exception.getMessage());
    }
}