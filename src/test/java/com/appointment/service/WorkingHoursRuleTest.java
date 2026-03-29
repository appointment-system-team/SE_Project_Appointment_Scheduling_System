package com.appointment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

class WorkingHoursRuleTest {

    @Test
    void testAppointmentWithinWorkingHoursIsValid() {
        WorkingHoursRule rule = new WorkingHoursRule();

        Appointment appointment = new Appointment(
                new User("Maryam"),
                new TimeSlot(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0))),
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void testAppointmentBeforeWorkingHoursIsInvalid() {
        WorkingHoursRule rule = new WorkingHoursRule();

        Appointment appointment = new Appointment(
                new User("Maryam"),
                new TimeSlot(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(7, 0))),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertFalse(rule.isValid(appointment));
    }

    @Test
    void testAppointmentEndingAfterWorkingHoursIsInvalid() {
        WorkingHoursRule rule = new WorkingHoursRule();

        Appointment appointment = new Appointment(
                new User("Maryam"),
                new TimeSlot(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(15, 30))),
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertFalse(rule.isValid(appointment));
    }
}