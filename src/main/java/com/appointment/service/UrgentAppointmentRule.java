package com.appointment.service;

import com.appointment.domain.Appointment;

public class UrgentAppointmentRule implements AppointmentPurposeRule {

    private static final int MAX_URGENT_DURATION = 30;

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getDurationInMinutes() <= MAX_URGENT_DURATION;
    }
}