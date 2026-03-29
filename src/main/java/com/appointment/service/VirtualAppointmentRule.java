package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentPurpose;

public class VirtualAppointmentRule implements AppointmentModeRule {

    @Override
    public boolean isValid(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        return appointment.getAppointmentPurpose() != AppointmentPurpose.ASSESSMENT;
    }
}