package com.appointment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentType;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

class AppointmentRepositoryTest {

    @Test
    void shouldSaveAndReturnAppointments() {
        AppointmentRepository repository = new AppointmentRepository();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0)),
                60,
                2,
                AppointmentType.ASSESSMENT,
                AppointmentMode.IN_PERSON);

        repository.save(appointment);

        List<Appointment> result = repository.findAll();

        assertEquals(1, result.size());
        assertEquals(appointment, result.get(0));
    }

    @Test
    void shouldReturnDefensiveCopyFromFindAll() {
        AppointmentRepository repository = new AppointmentRepository();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0)),
                60,
                2,
                AppointmentType.ASSESSMENT,
                AppointmentMode.IN_PERSON);

        repository.save(appointment);

        List<Appointment> result1 = repository.findAll();
        List<Appointment> result2 = repository.findAll();

        assertNotSame(result1, result2);

        result1.clear();

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldReturnTrueWhenRepositoryContainsAppointment() {
        AppointmentRepository repository = new AppointmentRepository();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0)),
                60,
                2,
                AppointmentType.ASSESSMENT,
                AppointmentMode.IN_PERSON);

        repository.save(appointment);

        assertTrue(repository.contains(appointment));
    }

    @Test
    void shouldReturnFalseWhenRepositoryDoesNotContainAppointment() {
        AppointmentRepository repository = new AppointmentRepository();

        Appointment appointment = new Appointment(
                new User("Zeina"),
                new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0)),
                60,
                2,
                AppointmentType.ASSESSMENT,
                AppointmentMode.IN_PERSON);

        assertFalse(repository.contains(appointment));
    }
}