package com.appointment.service;

import com.appointment.domain.Appointment;

public class InPersonAppointmentRule implements AppointmentModeRule {

    @Override
    public boolean isValid(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        return true;
    }
}