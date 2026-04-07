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

class FollowUpAppointmentRuleTest {

    @Test
    void shouldReturnTrueWhenFollowUpAppointmentIsIndividual() {
        FollowUpAppointmentRule rule = new FollowUpAppointmentRule();

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
    void shouldReturnFalseWhenFollowUpAppointmentIsGroup() {
        FollowUpAppointmentRule rule = new FollowUpAppointmentRule();

        Appointment appointment = new Appointment(
        		new User("zeina1", "1234", "Zeina", "zeina@gmail.com", "0599000001"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                60,
                2,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.GROUP,
                AppointmentMode.VIRTUAL);

        assertFalse(rule.isValid(appointment));
    }
}