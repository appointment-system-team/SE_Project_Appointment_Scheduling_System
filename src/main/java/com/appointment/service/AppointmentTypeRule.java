package com.appointment.service;

import com.appointment.domain.Appointment;

public interface AppointmentTypeRule {

    boolean isValid(Appointment appointment);
}