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

class AppointmentServiceTest {

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

        user = new User("maryam1", "1234", "Maryam", "maryam@gmail.com", "0599000003");
    }

    private LocalDate getNextWorkingDay() {
        LocalDate date = LocalDate.now().plusDays(1);

        while (date.getDayOfWeek() == DayOfWeek.FRIDAY
                || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            date = date.plusDays(1);
        }

        return date;
    }

    private TimeSlot createValidTimeSlot(int daysFromNow, int hour, int minute) {
        LocalDate date = LocalDate.now().plusDays(daysFromNow);

        while (date.getDayOfWeek() == DayOfWeek.FRIDAY
                || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            date = date.plusDays(1);
        }

        TimeSlot timeSlot = new TimeSlot(LocalDateTime.of(date, LocalTime.of(hour, minute)));
        timeSlotRepository.addTimeSlot(timeSlot);
        return timeSlot;
    }

    @Test
    void testBookAppointmentSuccessfully() {
        TimeSlot slot = createValidTimeSlot(1, 10, 0);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertNotNull(appointment);
        assertTrue(slot.isBooked());
        assertEquals(user, appointment.getUser());
    }

    @Test
    void testCancelAppointmentByUserSuccessfully() {
        TimeSlot slot = createValidTimeSlot(1, 11, 0);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        service.cancelAppointmentByUser(appointment, user);

        assertTrue(appointment.isCancelled());
        assertFalse(slot.isBooked());
    }

    @Test
    void testCancelAppointmentByAdminSuccessfully() {
        TimeSlot slot = createValidTimeSlot(1, 12, 0);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        authService.login("admin", "1234");
        service.cancelAppointmentByAdmin(appointment);

        assertTrue(appointment.isCancelled());
        assertFalse(slot.isBooked());
    }

    @Test
    void testUserCannotCancelAnotherUsersAppointment() {
        TimeSlot slot = createValidTimeSlot(1, 13, 0);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        User anotherUser = new User("ali1", "1234", "Ali", "ali@gmail.com", "0599000005");

        assertThrows(IllegalStateException.class, () ->
                service.cancelAppointmentByUser(appointment, anotherUser));
    }

    @Test
    void testModifyAppointmentByUserSuccessfully() {
        TimeSlot originalSlot = createValidTimeSlot(1, 9, 0);
        TimeSlot newSlot = createValidTimeSlot(2, 10, 0);

        Appointment appointment = service.bookAppointment(
                user,
                originalSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        service.modifyAppointmentByUser(
                appointment,
                user,
                newSlot,
                60,
                1
        );

        assertEquals(newSlot, appointment.getTimeSlot());
        assertEquals(60, appointment.getDurationInMinutes());
        assertFalse(originalSlot.isBooked());
        assertTrue(newSlot.isBooked());
    }

    @Test
    void testModifyAppointmentByAdminSuccessfully() {
        TimeSlot originalSlot = createValidTimeSlot(1, 10, 0);
        TimeSlot newSlot = createValidTimeSlot(2, 11, 0);

        Appointment appointment = service.bookAppointment(
                user,
                originalSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        authService.login("admin", "1234");

        service.modifyAppointmentByAdmin(
                appointment,
                newSlot,
                45,
                1
        );

        assertEquals(newSlot, appointment.getTimeSlot());
        assertEquals(45, appointment.getDurationInMinutes());
        assertFalse(originalSlot.isBooked());
        assertTrue(newSlot.isBooked());
    }

    @Test
    void testUserCannotModifyAnotherUsersAppointment() {
        TimeSlot originalSlot = createValidTimeSlot(1, 9, 30);
        TimeSlot newSlot = createValidTimeSlot(2, 12, 0);

        Appointment appointment = service.bookAppointment(
                user,
                originalSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        User anotherUser = new User("ali1", "1234", "Ali", "ali@gmail.com", "0599000005");

        assertThrows(IllegalStateException.class, () ->
                service.modifyAppointmentByUser(
                        appointment,
                        anotherUser,
                        newSlot,
                        60,
                        1
                ));
    }

    @Test
    void testAdminCannotModifyWithoutLogin() {
        TimeSlot originalSlot = createValidTimeSlot(1, 10, 30);
        TimeSlot newSlot = createValidTimeSlot(2, 13, 0);

        Appointment appointment = service.bookAppointment(
                user,
                originalSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalStateException.class, () ->
                service.modifyAppointmentByAdmin(
                        appointment,
                        newSlot,
                        60,
                        1
                ));
    }

    @Test
    void testCannotModifyPastAppointment() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);
        TimeSlot pastSlot = new TimeSlot(pastDateTime);
        timeSlotRepository.addTimeSlot(pastSlot);

        Appointment appointment = new Appointment(
                user,
                pastSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );
        appointmentRepository.save(appointment);

        TimeSlot newSlot = createValidTimeSlot(2, 10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.modifyAppointmentByUser(
                        appointment,
                        user,
                        newSlot,
                        60,
                        1
                ));
    }

    @Test
    void testCannotCancelPastAppointment() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);
        TimeSlot pastSlot = new TimeSlot(pastDateTime);
        timeSlotRepository.addTimeSlot(pastSlot);

        Appointment appointment = new Appointment(
                user,
                pastSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );
        appointmentRepository.save(appointment);

        assertThrows(IllegalArgumentException.class, () ->
                service.cancelAppointmentByUser(appointment, user));
    }

    @Test
    void testPreventOverlappingAppointments() {
        TimeSlot slot1 = createValidTimeSlot(1, 12, 0);
        TimeSlot slot2 = createValidTimeSlot(1, 13, 0);

        service.bookAppointment(
                user,
                slot1,
                120,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user,
                        slot2,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON
                ));
    }

    @Test
    void testRejectFridayBooking() {
        LocalDate date = LocalDate.now().plusDays(1);

        while (date.getDayOfWeek() != DayOfWeek.FRIDAY) {
            date = date.plusDays(1);
        }

        TimeSlot fridaySlot = new TimeSlot(LocalDateTime.of(date, LocalTime.of(10, 0)));
        timeSlotRepository.addTimeSlot(fridaySlot);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user,
                        fridaySlot,
                        30,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON
                ));
    }

    @Test
    void testRejectSaturdayBooking() {
        LocalDate date = LocalDate.now().plusDays(1);

        while (date.getDayOfWeek() != DayOfWeek.SATURDAY) {
            date = date.plusDays(1);
        }

        TimeSlot saturdaySlot = new TimeSlot(LocalDateTime.of(date, LocalTime.of(10, 0)));
        timeSlotRepository.addTimeSlot(saturdaySlot);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user,
                        saturdaySlot,
                        30,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON
                ));
    }

    @Test
    void testRejectBookingBeforeWorkingHours() {
        TimeSlot earlySlot = createValidTimeSlot(1, 7, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user,
                        earlySlot,
                        30,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON
                ));
    }

    @Test
    void testRejectBookingAfterWorkingHours() {
        TimeSlot lateSlot = createValidTimeSlot(1, 15, 30);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user,
                        lateSlot,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON
                ));
    }

    @Test
    void testRejectAssessmentAsVirtualAppointment() {
        TimeSlot slot = createValidTimeSlot(1, 10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                service.bookAppointment(
                        user,
                        slot,
                        30,
                        1,
                        AppointmentPurpose.ASSESSMENT,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL
                ));
    }

    @Test
    void testGetAvailableSlotsReturnsOnlyUnbookedSlots() {
        TimeSlot slot1 = createValidTimeSlot(1, 9, 0);
        TimeSlot slot2 = createValidTimeSlot(1, 11, 0);

        service.bookAppointment(
                user,
                slot1,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertFalse(service.getAvailableSlots().contains(slot1));
        assertTrue(service.getAvailableSlots().contains(slot2));
    }
}