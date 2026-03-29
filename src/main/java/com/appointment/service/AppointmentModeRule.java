package com.appointment.service;

import com.appointment.domain.Appointment;

public interface AppointmentModeRule {
    boolean isValid(Appointment appointment);
}