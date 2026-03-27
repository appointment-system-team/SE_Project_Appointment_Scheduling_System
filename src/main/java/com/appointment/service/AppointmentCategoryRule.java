package com.appointment.service;

import com.appointment.domain.Appointment;

public interface AppointmentCategoryRule {

    boolean isValid(Appointment appointment);
}