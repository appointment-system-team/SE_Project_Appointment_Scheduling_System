package com.appointment.service;

import com.appointment.domain.AppointmentMode;

public class AppointmentModeRuleFactory {

    public AppointmentModeRule getRule(AppointmentMode appointmentMode) {
        if (appointmentMode == null) {
            throw new IllegalArgumentException("Appointment mode cannot be null.");
        }

        switch (appointmentMode) {
            case VIRTUAL:
                return new VirtualAppointmentRule();
            case IN_PERSON:
                return new InPersonAppointmentRule();
            default:
                throw new IllegalArgumentException("Unsupported appointment mode.");
        }
    }
}