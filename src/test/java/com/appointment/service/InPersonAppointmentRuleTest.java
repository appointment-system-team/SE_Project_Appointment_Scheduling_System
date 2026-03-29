package com.appointment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

class InPersonAppointmentRuleTest {

    @Test
    void testInPersonAppointmentIsAlwaysValid() {
        InPersonAppointmentRule rule = new InPersonAppointmentRule();

        Appointment appointment = new Appointment(
                new User("Maryam"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                30,
                1,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertTrue(rule.isValid(appointment));
    }
}