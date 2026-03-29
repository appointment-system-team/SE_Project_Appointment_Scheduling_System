package com.appointment.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.repository.AdminRepository;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.TimeSlotRepository;

class AppointmentServiceEdgeCasesTest {

    private AppointmentService service;
    private AppointmentRepository appointmentRepository;
    private TimeSlotRepository timeSlotRepository;
    private AuthenticationService authService;
    private User user;

    @BeforeEach
    void setUp() {
        timeSlotRepository = new TimeSlotRepository();
        appointmentRepository = new AppointmentRepository();
        authService = new AuthenticationService(new AdminRepository());

        service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authService
        );

        user = new User("Maryam");
    }

    private TimeSlot createSlot(int hour, int minute) {
        LocalDate date = LocalDate.now().plusDays(1);

        while (date.getDayOfWeek() == DayOfWeek.FRIDAY ||
               date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            date = date.plusDays(1);
        }

        TimeSlot slot = new TimeSlot(LocalDateTime.of(date, LocalTime.of(hour, minute)));
        timeSlotRepository.addTimeSlot(slot);
        return slot;
    }

    @Test
    void testNullUser() {
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        null, slot, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testNullTimeSlot() {
        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, null, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testNullPurpose() {
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 30, 1,
                        null,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testNullCategory() {
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        null,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testNullMode() {
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        null));
    }

    @Test
    void testSlotAlreadyBooked() {
        TimeSlot slot = createSlot(10, 0);

        service.bookAppointment(
                user, slot, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testSlotNotInRepository() {
        TimeSlot slot = new TimeSlot(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testModifyToBookedSlotFails() {
        TimeSlot slot1 = createSlot(9, 0);
        TimeSlot slot2 = createSlot(10, 0);

        Appointment a1 = service.bookAppointment(
                user, slot1, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        service.bookAppointment(
                new User("Ali"), slot2, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.modifyAppointmentByUser(a1, user, slot2, 30, 1));
    }

    @Test
    void testExactlyStartWorkingHourValid() {
        TimeSlot slot = createSlot(8, 0);

        assertDoesNotThrow(() ->
                service.bookAppointment(
                        user, slot, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testExactlyEndWorkingHourInvalid() {
        TimeSlot slot = createSlot(16, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 30, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testAdminCancelWithoutLoginFails() {
        TimeSlot slot = createSlot(10, 0);

        Appointment appointment = service.bookAppointment(
                user, slot, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalStateException.class, () ->
                service.cancelAppointmentByAdmin(appointment));
    }

    @Test
    void testInvalidDuration() {
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 10000, 1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testInvalidParticipantCount() {
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user, slot, 30, 1000,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));
    }

    @Test
    void testModifyWithSameSlot() {
        TimeSlot slot = createSlot(10, 0);

        Appointment appointment = service.bookAppointment(
                user, slot, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertDoesNotThrow(() ->
                service.modifyAppointmentByUser(
                        appointment,
                        user,
                        slot,
                        30,
                        1
                ));
    }

    @Test
    void testModifyWithNullUser() {
        TimeSlot originalSlot = createSlot(10, 0);
        TimeSlot newSlot = createSlot(11, 0);

        Appointment appointment = service.bookAppointment(
                user, originalSlot, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.modifyAppointmentByUser(
                        appointment,
                        null,
                        newSlot,
                        30,
                        1
                ));
    }

    @Test
    void testCancelWithNullUser() {
        TimeSlot slot = createSlot(10, 0);

        Appointment appointment = service.bookAppointment(
                user, slot, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.cancelAppointmentByUser(appointment, null));
    }

    @Test
    void testModifyWithNullNewTimeSlot() {
        TimeSlot slot = createSlot(10, 0);

        Appointment appointment = service.bookAppointment(
                user, slot, 30, 1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.modifyAppointmentByUser(
                        appointment,
                        user,
                        null,
                        30,
                        1
                ));
    }
}