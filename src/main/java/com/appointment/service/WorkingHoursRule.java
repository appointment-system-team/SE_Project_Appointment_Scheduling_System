package com.appointment.service;

import java.time.LocalTime;

import com.appointment.domain.Appointment;

public class WorkingHoursRule {

    private static final LocalTime START_OF_WORK = LocalTime.of(8, 0);
    private static final LocalTime END_OF_WORK = LocalTime.of(16, 0);

    public boolean isValid(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        LocalTime appointmentStart = appointment.getTimeSlot().getStartTime().toLocalTime();
        LocalTime appointmentEnd = appointment.getEndTime().toLocalTime();

        return !appointmentStart.isBefore(START_OF_WORK)
                && !appointmentEnd.isAfter(END_OF_WORK);
    }
}