package com.appointment.service;

import com.appointment.domain.AppointmentType;

public class AppointmentTypeRuleFactory {

    public AppointmentTypeRule getRule(AppointmentType appointmentType) {
        if (appointmentType == null) {
            throw new IllegalArgumentException("Appointment type cannot be null.");
        }

        switch (appointmentType) {
            case URGENT:
                return new UrgentAppointmentRule();
            case FOLLOW_UP:
                return new FollowUpAppointmentRule();
            case ASSESSMENT:
                return new AssessmentAppointmentRule();
            default:
                throw new IllegalArgumentException("Unsupported appointment type.");
        }
    }
}