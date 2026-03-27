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

class UrgentAppointmentRuleTest {

    @Test
    void shouldReturnTrueWhenUrgentAppointmentDurationIsWithinLimit() {
        UrgentAppointmentRule rule = new UrgentAppointmentRule();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                30,
                1,
                AppointmentPurpose.URGENT,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void shouldReturnFalseWhenUrgentAppointmentDurationExceedsLimit() {
        UrgentAppointmentRule rule = new UrgentAppointmentRule();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                45,
                1,
                AppointmentPurpose.URGENT,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        assertFalse(rule.isValid(appointment));
    }
}