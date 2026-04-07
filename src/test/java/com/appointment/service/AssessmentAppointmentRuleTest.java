package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

class AssessmentAppointmentRuleTest {

    @Test
    void shouldReturnTrueWhenAssessmentAppointmentIsInPerson() {
        AssessmentAppointmentRule rule = new AssessmentAppointmentRule();

        Appointment appointment = new Appointment(
                new User("zeina1", "1234", "Zeina", "zeina@gmail.com", "0599000001"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                1,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON);

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void shouldReturnFalseWhenAssessmentAppointmentIsVirtual() {
        AssessmentAppointmentRule rule = new AssessmentAppointmentRule();

        Appointment appointment = new Appointment(
                new User("zeina1", "1234", "Zeina", "zeina@gmail.com", "0599000001"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                1,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        assertFalse(rule.isValid(appointment));
    }
}