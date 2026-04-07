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

class VirtualAppointmentRuleTest {

    @Test
    void testAssessmentVirtualAppointmentIsInvalid() {
        VirtualAppointmentRule rule = new VirtualAppointmentRule();

        Appointment appointment = new Appointment(
        		new User("maryam1", "1234", "Maryam", "maryam@gmail.com", "0599000003"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                30,
                1,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        assertFalse(rule.isValid(appointment));
    }

    @Test
    void testFollowUpVirtualAppointmentIsValid() {
        VirtualAppointmentRule rule = new VirtualAppointmentRule();

        Appointment appointment = new Appointment(
        		new User("maryam1", "1234", "Maryam", "maryam@gmail.com", "0599000003"),
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        assertTrue(rule.isValid(appointment));
    }
}