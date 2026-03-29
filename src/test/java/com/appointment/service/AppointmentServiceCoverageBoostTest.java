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

class AppointmentServiceCoverageBoostTest {

    private TimeSlotRepository timeSlotRepository;
    private AppointmentRepository appointmentRepository;
    private AuthenticationService authService;
    private AppointmentService service;
    private User user;

    @BeforeEach
    void setUp() {
        timeSlotRepository = new TimeSlotRepository();
        appointmentRepository = new AppointmentRepository();
        authService = new AuthenticationService(new AdminRepository());
        service = new AppointmentService(timeSlotRepository, appointmentRepository, authService);
        user = new User("Maryam");
    }

    private TimeSlot createSlot(int hour, int minute) {
        LocalDate date = LocalDate.now().plusDays(1);

        while (date.getDayOfWeek() == DayOfWeek.FRIDAY
                || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            date = date.plusDays(1);
        }

        TimeSlot slot = new TimeSlot(LocalDateTime.of(date, LocalTime.of(hour, minute)));
        timeSlotRepository.addTimeSlot(slot);
        return slot;
    }

    @Test
    void testConstructorWithOnlyTimeSlotRepositoryFailsOnBooking() {
        AppointmentService localService = new AppointmentService(timeSlotRepository);
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalStateException.class, () ->
                localService.bookAppointment(
                        user,
                        slot,
                        30,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON
                ));
    }

    @Test
    void testConstructorWithRepositoryAndNoAuthFailsOnAdminCancel() {
        AppointmentService localService = new AppointmentService(timeSlotRepository, appointmentRepository);
        TimeSlot slot = createSlot(10, 0);

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );
        appointmentRepository.save(appointment);

        assertThrows(IllegalStateException.class, () ->
                localService.cancelAppointmentByAdmin(appointment));
    }

    @Test
    void testCancelNullAppointmentFails() {
        assertThrows(IllegalStateException.class, () ->
                service.cancelAppointmentByAdmin(null));
    }

    @Test
    void testModifyAppointmentThatDoesNotExistFails() {
        TimeSlot slot = createSlot(10, 0);
        TimeSlot newSlot = createSlot(11, 0);

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertThrows(IllegalArgumentException.class, () ->
                service.modifyAppointmentByUser(appointment, user, newSlot, 30, 1));
    }

    @Test
    void testCancelAlreadyCancelledAppointmentFails() {
        TimeSlot slot = createSlot(10, 0);

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

        assertThrows(IllegalStateException.class, () ->
                service.cancelAppointmentByAdmin(appointment));
    }

    @Test
    void testModifyToSlotNotInRepositoryFails() {
        TimeSlot slot = createSlot(10, 0);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        TimeSlot externalSlot = new TimeSlot(LocalDateTime.now().plusDays(2));

        assertThrows(IllegalArgumentException.class, () ->
                service.modifyAppointmentByUser(appointment, user, externalSlot, 30, 1));
    }

    @Test
    void testModifyToPastSlotFails() {
        TimeSlot slot = createSlot(10, 0);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        TimeSlot pastSlot = new TimeSlot(LocalDateTime.now().minusDays(1));
        timeSlotRepository.addTimeSlot(pastSlot);

        assertThrows(IllegalArgumentException.class, () ->
                service.modifyAppointmentByUser(appointment, user, pastSlot, 30, 1));
    }

    @Test
    void testAddNullObserverDoesNotThrow() {
        assertDoesNotThrow(() -> service.addObserver(null));
    }

    @Test
    void testAddObserverAndNotify() {
        TestObserver observer = new TestObserver();
        TimeSlot slot = createSlot(10, 0);

        service.addObserver(observer);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertEquals(1, observer.count);
    }

    @Test
    void testRemoveObserverStopsNotification() {
        TestObserver observer = new TestObserver();
        TimeSlot slot = createSlot(10, 0);

        service.addObserver(observer);
        service.removeObserver(observer);

        service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.IN_PERSON
        );

        assertEquals(0, observer.count);
    }

    @Test
    void testVirtualFollowUpPassesModeRule() {
        TimeSlot slot = createSlot(10, 0);

        assertDoesNotThrow(() ->
                service.bookAppointment(
                        user,
                        slot,
                        30,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL
                ));
    }

    @Test
    void testBookingWithoutRepositoryFails() {
        AppointmentService localService = new AppointmentService(timeSlotRepository);
        TimeSlot slot = createSlot(10, 0);

        assertThrows(IllegalStateException.class, () ->
                localService.bookAppointment(
                        user,
                        slot,
                        30,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON
                ));
    }

    private static class TestObserver implements AppointmentObserver {
        int count = 0;

        @Override
        public void update(Appointment appointment, String eventType) {
            count++;
        }
    }
}