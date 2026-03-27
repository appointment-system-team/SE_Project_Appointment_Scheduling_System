package com.appointment.service;

import com.appointment.domain.Appointment;

public interface AppointmentPurposeRule {

    boolean isValid(Appointment appointment);
}