package com.appointment.service;

import com.appointment.domain.Appointment;

public class FollowUpAppointmentRule implements AppointmentTypeRule {

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getParticipantCount() == 1;
    }
}