package com.appointment.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

class AppointmentRepositoryTest {

    private AppointmentRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        repository = new AppointmentRepository();
        user = new User("maryam1", "1234", "Maryam", "maryam@gmail.com", "0599000003");
    }

    @Test
    void testSaveAndFindAll() {
        Appointment appointment = new Appointment(
                user,
                new TimeSlot(LocalDateTime.now().plusDays(1)),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        repository.save(appointment);

        assertEquals(1, repository.findAll().size());
        assertTrue(repository.contains(appointment));
    }

    @Test
    void testFindByUser() {
        User anotherUser = new User("ahmad1", "1234", "Ahmad", "ahmad@gmail.com", "0599000004");

        Appointment appointment1 = new Appointment(
                user,
                new TimeSlot(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0)),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        Appointment appointment2 = new Appointment(
                anotherUser,
                new TimeSlot(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0)),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        repository.save(appointment1);
        repository.save(appointment2);

        List<Appointment> userAppointments = repository.findByUser(user);

        assertEquals(1, userAppointments.size());
        assertTrue(userAppointments.contains(appointment1));
        assertFalse(userAppointments.contains(appointment2));
    }

    @Test
    void testHasOverlappingAppointmentReturnsTrue() {
        Appointment existingAppointment = new Appointment(
                user,
                new TimeSlot(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0)),
                120,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        repository.save(existingAppointment);

        Appointment newAppointment = new Appointment(
                user,
                new TimeSlot(LocalDateTime.now().plusDays(1).withHour(13).withMinute(0)),
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        boolean result = repository.hasOverlappingAppointment(user, null, newAppointment);

        assertTrue(result);
    }

    @Test
    void testHasOverlappingAppointmentReturnsFalse() {
        Appointment existingAppointment = new Appointment(
                user,
                new TimeSlot(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0)),
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        repository.save(existingAppointment);

        Appointment newAppointment = new Appointment(
                user,
                new TimeSlot(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0)),
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        boolean result = repository.hasOverlappingAppointment(user, null, newAppointment);

        assertFalse(result);
    }
}