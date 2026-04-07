package com.appointment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Administrator;
import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.repository.AdminRepository;

public class NotificationObserverTest {

    @Test
    void shouldSendNotificationToUserWhenObserverIsUpdatedWithBookedEvent() {
        NotificationService notificationService = mock(NotificationService.class);
        AdminRepository adminRepository = new AdminRepository();
        NotificationObserver observer = new NotificationObserver(notificationService, adminRepository);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0)
        );

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        observer.update(appointment, "BOOKED");

        verify(notificationService, times(1))
                .notifyUser(any(User.class), contains("booked successfully"));
        verify(notificationService, never())
                .notifyAdmin(any(Administrator.class), any(String.class));
    }

    @Test
    void shouldSendNotificationToUserAndAdminWhenUserCancelsAppointment() {
        NotificationService notificationService = mock(NotificationService.class);
        AdminRepository adminRepository = new AdminRepository();
        NotificationObserver observer = new NotificationObserver(notificationService, adminRepository);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0)
        );

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        observer.update(appointment, "USER_CANCELLED");

        verify(notificationService, times(1))
                .notifyUser(any(User.class), contains("cancelled successfully"));
        verify(notificationService, times(1))
                .notifyAdmin(any(Administrator.class), contains("cancelled their appointment"));
    }

    @Test
    void shouldSendNotificationToUserOnlyWhenAdminCancelsAppointment() {
        NotificationService notificationService = mock(NotificationService.class);
        AdminRepository adminRepository = new AdminRepository();
        NotificationObserver observer = new NotificationObserver(notificationService, adminRepository);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0)
        );

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        observer.update(appointment, "ADMIN_CANCELLED");

        verify(notificationService, times(1))
                .notifyUser(any(User.class), contains("cancelled by the administrator"));
        verify(notificationService, never())
                .notifyAdmin(any(Administrator.class), any(String.class));
    }

    @Test
    void shouldSendNotificationToUserAndAdminWhenUserModifiesAppointment() {
        NotificationService notificationService = mock(NotificationService.class);
        AdminRepository adminRepository = new AdminRepository();
        NotificationObserver observer = new NotificationObserver(notificationService, adminRepository);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0)
        );

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        observer.update(appointment, "USER_MODIFIED");

        verify(notificationService, times(1))
                .notifyUser(any(User.class), contains("modified successfully"));
        verify(notificationService, times(1))
                .notifyAdmin(any(Administrator.class), contains("modified their appointment"));
    }

    @Test
    void shouldSendNotificationToUserOnlyWhenAdminModifiesAppointment() {
        NotificationService notificationService = mock(NotificationService.class);
        AdminRepository adminRepository = new AdminRepository();
        NotificationObserver observer = new NotificationObserver(notificationService, adminRepository);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0)
        );

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        observer.update(appointment, "ADMIN_MODIFIED");

        verify(notificationService, times(1))
                .notifyUser(any(User.class), contains("modified by the administrator"));
        verify(notificationService, never())
                .notifyAdmin(any(Administrator.class), any(String.class));
    }
    
    @Test
    void shouldNotSendAnyNotificationForUnknownEvent() {
        NotificationService notificationService = mock(NotificationService.class);
        AdminRepository adminRepository = new AdminRepository();
        NotificationObserver observer = new NotificationObserver(notificationService, adminRepository);

        User user = new User("jana1", "1234", "Jana", "jana@gmail.com", "0599000006");
        TimeSlot slot = new TimeSlot(
                LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0)
        );

        Appointment appointment = new Appointment(
                user,
                slot,
                30,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL
        );

        observer.update(appointment, "UNKNOWN");

        verify(notificationService, never()).notifyUser(any(User.class), any(String.class));
        verify(notificationService, never()).notifyAdmin(any(Administrator.class), any(String.class));
    }
}