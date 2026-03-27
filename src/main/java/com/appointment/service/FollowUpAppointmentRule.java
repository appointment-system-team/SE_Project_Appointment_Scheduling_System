package com.appointment.service;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;

public class FollowUpAppointmentRule implements AppointmentPurposeRule {

    @Override
    public boolean isValid(Appointment appointment) {
        return appointment.getAppointmentCategory() == AppointmentCategory.INDIVIDUAL;
    }
}