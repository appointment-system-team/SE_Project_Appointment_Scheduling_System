package com.appointment.service;

import java.time.DayOfWeek;

import com.appointment.domain.Appointment;

public class WorkingDaysRule {

    public boolean isValid(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        DayOfWeek day = appointment.getTimeSlot().getStartTime().getDayOfWeek();

        return day != DayOfWeek.FRIDAY && day != DayOfWeek.SATURDAY;
    }
}