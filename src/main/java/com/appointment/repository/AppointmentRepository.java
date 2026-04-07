package com.appointment.repository;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.Appointment;
import com.appointment.domain.User;

public class AppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();

    public void save(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }

    public List<Appointment> findByUser(User user) {
        List<Appointment> userAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getUser().equals(user)) {
                userAppointments.add(appointment);
            }
        }

        return userAppointments;
    }

    public boolean contains(Appointment appointment) {
        return appointments.contains(appointment);
    }

    public boolean hasOverlappingAppointment(
            User user,
            Appointment excludedAppointment,
            Appointment targetAppointment) {

        for (Appointment appointment : appointments) {
            if (appointment == excludedAppointment) {
                continue;
            }

            if (appointment.isCancelled()) {
                continue;
            }

            if (appointment.getUser().equals(user) && appointment.overlapsWith(targetAppointment)) {
                return true;
            }
        }

        return false;
    }
}