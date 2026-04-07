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

class IndividualAppointmentRuleTest {

    @Test
    void shouldReturnTrueWhenIndividualAppointmentHasOneParticipant() {
        IndividualAppointmentRule rule = new IndividualAppointmentRule();

        Appointment appointment = new Appointment(
        		new User("zeina1", "1234", "Zeina", "zeina@gmail.com", "0599000001"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void shouldReturnFalseWhenIndividualAppointmentHasMoreThanOneParticipant() {
        IndividualAppointmentRule rule = new IndividualAppointmentRule();

        Appointment appointment = new Appointment(
        		new User("zeina1", "1234", "Zeina", "zeina@gmail.com", "0599000001"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                2,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        assertFalse(rule.isValid(appointment));
    }
}