package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

class AssessmentAppointmentRuleTest {

    @Test
    void shouldReturnTrueWhenAssessmentAppointmentIsInPerson() {
        AssessmentAppointmentRule rule = new AssessmentAppointmentRule();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                1,
                AppointmentType.ASSESSMENT,
                AppointmentMode.IN_PERSON);

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void shouldReturnFalseWhenAssessmentAppointmentIsVirtual() {
        AssessmentAppointmentRule rule = new AssessmentAppointmentRule();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                1,
                AppointmentType.ASSESSMENT,
                AppointmentMode.VIRTUAL);

        assertFalse(rule.isValid(appointment));
    }
}