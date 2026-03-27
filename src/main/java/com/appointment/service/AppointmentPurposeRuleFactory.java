package com.appointment.service;

import com.appointment.domain.AppointmentPurpose;

public class AppointmentPurposeRuleFactory {

    public AppointmentPurposeRule getRule(AppointmentPurpose appointmentPurpose) {
        if (appointmentPurpose == null) {
            throw new IllegalArgumentException("Appointment purpose cannot be null.");
        }

        switch (appointmentPurpose) {
            case URGENT:
                return new UrgentAppointmentRule();
            case FOLLOW_UP:
                return new FollowUpAppointmentRule();
            case ASSESSMENT:
                return new AssessmentAppointmentRule();
            default:
                throw new IllegalArgumentException("Unsupported appointment purpose.");
        }
    }
}