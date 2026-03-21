package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentMode;

public class AssessmentAppointmentRule implements AppointmentTypeRule {

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getAppointmentMode() == AppointmentMode.IN_PERSON;
    }
}