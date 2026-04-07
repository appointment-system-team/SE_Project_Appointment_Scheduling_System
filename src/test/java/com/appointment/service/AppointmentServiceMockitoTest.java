package com.appointment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

class AppointmentServiceMockitoTest {

    @Test
    void shouldNotifyObserverWhenAppointmentIsBooked() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-07T08:00:00Z"),
                ZoneId.systemDefault()
        );

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                null,
                fixedClock
        );

        AppointmentObserver observer = mock(AppointmentObserver.class);
        service.addObserver(observer);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 4, 8, 10, 0));
        timeSlotRepository.addTimeSlot(slot);

        service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        verify(observer, times(1)).update(any(Appointment.class), eq("BOOKED"));
    }

    @Test
    void shouldNotifyObserverWhenUserCancelsAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-07T08:00:00Z"),
                ZoneId.systemDefault()
        );

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                null,
                fixedClock
        );

        AppointmentObserver observer = mock(AppointmentObserver.class);
        service.addObserver(observer);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 4, 8, 10, 0));
        timeSlotRepository.addTimeSlot(slot);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        service.cancelAppointmentByUser(appointment, user);

        verify(observer, times(1)).update(any(Appointment.class), eq("BOOKED"));
        verify(observer, times(1)).update(any(Appointment.class), eq("USER_CANCELLED"));
    }

    @Test
    void shouldNotifyObserverWhenUserModifiesAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-07T08:00:00Z"),
                ZoneId.systemDefault()
        );

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                null,
                fixedClock
        );

        AppointmentObserver observer = mock(AppointmentObserver.class);
        service.addObserver(observer);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot originalSlot = new TimeSlot(LocalDateTime.of(2026, 4, 8, 10, 0));
        TimeSlot newSlot = new TimeSlot(LocalDateTime.of(2026, 4, 9, 11, 0));

        timeSlotRepository.addTimeSlot(originalSlot);
        timeSlotRepository.addTimeSlot(newSlot);

        Appointment appointment = service.bookAppointment(
                user,
                originalSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        service.modifyAppointmentByUser(
                appointment,
                user,
                newSlot,
                30,
                1
        );

        verify(observer, times(1)).update(any(Appointment.class), eq("BOOKED"));
        verify(observer, times(1)).update(any(Appointment.class), eq("USER_MODIFIED"));
    }

    @Test
    void shouldNotifyObserverWhenAdminCancelsAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AdminRepository adminRepository = new AdminRepository();
        AuthenticationService authenticationService = new AuthenticationService(adminRepository);

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-07T08:00:00Z"),
                ZoneId.systemDefault()
        );

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock
        );

        AppointmentObserver observer = mock(AppointmentObserver.class);
        service.addObserver(observer);

        authenticationService.login("admin", "1234");

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2026, 4, 8, 10, 0));
        timeSlotRepository.addTimeSlot(slot);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        service.cancelAppointmentByAdmin(appointment);

        verify(observer, times(1)).update(any(Appointment.class), eq("BOOKED"));
        verify(observer, times(1)).update(any(Appointment.class), eq("ADMIN_CANCELLED"));
    }

    @Test
    void shouldNotifyObserverWhenAdminModifiesAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AdminRepository adminRepository = new AdminRepository();
        AuthenticationService authenticationService = new AuthenticationService(adminRepository);

        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-04-07T08:00:00Z"),
                ZoneId.systemDefault()
        );

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock
        );

        AppointmentObserver observer = mock(AppointmentObserver.class);
        service.addObserver(observer);

        authenticationService.login("admin", "1234");

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot originalSlot = new TimeSlot(LocalDateTime.of(2026, 4, 8, 10, 0));
        TimeSlot newSlot = new TimeSlot(LocalDateTime.of(2026, 4, 9, 11, 0));

        timeSlotRepository.addTimeSlot(originalSlot);
        timeSlotRepository.addTimeSlot(newSlot);

        Appointment appointment = service.bookAppointment(
                user,
                originalSlot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        service.modifyAppointmentByAdmin(
                appointment,
                newSlot,
                30,
                1
        );

        verify(observer, times(1)).update(any(Appointment.class), eq("BOOKED"));
        verify(observer, times(1)).update(any(Appointment.class), eq("ADMIN_MODIFIED"));
    }
}