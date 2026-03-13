package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.repository.AppointmentRepository;

public class ReminderServiceTest {

    @Test
    void shouldSendReminderMessage() {

        AppointmentRepository repository = new AppointmentRepository();

        User user = new User("Jana");

        TimeSlot slot =
                new TimeSlot(LocalDateTime.now().plusHours(2));

        Appointment appointment =
                new Appointment(user, slot, 30, 1);

        repository.save(appointment);

        MockNotificationService mockService =
                new MockNotificationService();

        ReminderService reminderService =
                new ReminderService(repository, mockService);

        reminderService.sendReminders();

        assertEquals(1, mockService.getSentMessages().size());
    }
}