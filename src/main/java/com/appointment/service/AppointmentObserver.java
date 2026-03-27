package com.appointment.service;

import com.appointment.domain.Appointment;

public interface AppointmentObserver {
    void update(Appointment appointment, String eventType);
}