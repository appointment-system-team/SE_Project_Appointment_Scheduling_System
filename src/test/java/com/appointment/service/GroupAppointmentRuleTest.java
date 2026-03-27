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

class GroupAppointmentRuleTest {

    @Test
    void shouldReturnTrueWhenGroupAppointmentHasMoreThanOneParticipant() {
        GroupAppointmentRule rule = new GroupAppointmentRule();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                3,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void shouldReturnFalseWhenGroupAppointmentHasOnlyOneParticipant() {
        GroupAppointmentRule rule = new GroupAppointmentRule();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                1,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        assertFalse(rule.isValid(appointment));
    }
}