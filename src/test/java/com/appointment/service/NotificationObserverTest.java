package com.appointment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;

public class NotificationObserverTest {

    @Test
    void shouldSendNotificationWhenObserverIsUpdated() {
        NotificationService notificationService = mock(NotificationService.class);
        NotificationObserver observer = new NotificationObserver(notificationService);

        User user = new User("Jana");
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        observer.update(appointment, "BOOKED");

        verify(notificationService, times(1))
                .notify(any(User.class), contains("Appointment BOOKED:"));
    }
}