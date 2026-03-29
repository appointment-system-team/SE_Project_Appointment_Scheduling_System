package com.appointment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
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

class WorkingDaysRuleTest {

    private LocalDate nextDateForDay(DayOfWeek dayOfWeek) {
        LocalDate date = LocalDate.now().plusDays(1);

        while (date.getDayOfWeek() != dayOfWeek) {
            date = date.plusDays(1);
        }

        return date;
    }

    @Test
    void testSundayIsValidWorkingDay() {
        WorkingDaysRule rule = new WorkingDaysRule();

        Appointment appointment = new Appointment(
                new User("Maryam"),
                new TimeSlot(LocalDateTime.of(nextDateForDay(DayOfWeek.SUNDAY), LocalTime.of(10, 0))),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertTrue(rule.isValid(appointment));
    }

    @Test
    void testFridayIsInvalidWorkingDay() {
        WorkingDaysRule rule = new WorkingDaysRule();

        Appointment appointment = new Appointment(
                new User("Maryam"),
                new TimeSlot(LocalDateTime.of(nextDateForDay(DayOfWeek.FRIDAY), LocalTime.of(10, 0))),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertFalse(rule.isValid(appointment));
    }

    @Test
    void testSaturdayIsInvalidWorkingDay() {
        WorkingDaysRule rule = new WorkingDaysRule();

        Appointment appointment = new Appointment(
                new User("Maryam"),
                new TimeSlot(LocalDateTime.of(nextDateForDay(DayOfWeek.SATURDAY), LocalTime.of(10, 0))),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertFalse(rule.isValid(appointment));
    }
}