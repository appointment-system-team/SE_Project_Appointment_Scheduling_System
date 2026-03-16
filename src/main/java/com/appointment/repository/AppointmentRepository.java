package com.appointment.repository;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.Appointment;

public class AppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();

    public void save(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }

    public boolean contains(Appointment appointment) {
        return appointments.contains(appointment);
    }
}