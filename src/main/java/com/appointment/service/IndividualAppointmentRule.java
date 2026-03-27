package com.appointment.service;

import com.appointment.domain.Appointment;

public class IndividualAppointmentRule implements AppointmentCategoryRule {

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getParticipantCount() == 1;
    }
}