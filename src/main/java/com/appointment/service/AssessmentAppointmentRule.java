package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentMode;

public class AssessmentAppointmentRule implements AppointmentPurposeRule {

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getAppointmentMode() == AppointmentMode.IN_PERSON;
    }
}