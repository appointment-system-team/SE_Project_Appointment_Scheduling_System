package com.appointment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import com.appointment.repository.AppointmentRepository;

public class ReminderServiceTest {

    @Test
    void shouldSendReminderMessageForFutureAppointment() {
        AppointmentRepository repository = new AppointmentRepository();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(LocalDateTime.now(fixedClock).plusHours(2));

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        repository.save(appointment);

        NotificationService notificationService = mock(NotificationService.class);
        ReminderService reminderService = new ReminderService(repository, notificationService, fixedClock);

        reminderService.sendReminders();

        verify(notificationService, times(1))
                .notifyUser(any(User.class), contains("Reminder: You have an appointment at"));
    }

    @Test
    void shouldNotSendReminderForPastAppointment() {
        AppointmentRepository repository = new AppointmentRepository();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(LocalDateTime.now(fixedClock).minusHours(2));

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        repository.save(appointment);

        NotificationService notificationService = mock(NotificationService.class);
        ReminderService reminderService = new ReminderService(repository, notificationService, fixedClock);

        reminderService.sendReminders();

        verify(notificationService, never()).notifyUser(any(User.class), any(String.class));
    }

    @Test
    void shouldNotSendReminderForCancelledAppointment() {
        AppointmentRepository repository = new AppointmentRepository();

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(LocalDateTime.now(fixedClock).plusHours(2));

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        appointment.cancel();
        repository.save(appointment);

        NotificationService notificationService = mock(NotificationService.class);
        ReminderService reminderService = new ReminderService(repository, notificationService, fixedClock);

        reminderService.sendReminders();

        verify(notificationService, never()).notifyUser(any(User.class), any(String.class));
    }
}