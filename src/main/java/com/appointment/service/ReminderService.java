package com.appointment.service;

import java.time.Clock;
import java.util.List;

import com.appointment.domain.Appointment;
import com.appointment.repository.AppointmentRepository;

public class ReminderService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    public ReminderService(
            AppointmentRepository appointmentRepository,
            NotificationService notificationService) {
        this(appointmentRepository, notificationService, Clock.systemDefaultZone());
    }

    public ReminderService(
            AppointmentRepository appointmentRepository,
            NotificationService notificationService,
            Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    public void sendReminders() {
        List<Appointment> appointments = appointmentRepository.findAll();

        for (Appointment appointment : appointments) {
            if (!shouldSendReminder(appointment)) {
                continue;
            }

            String message = generateReminderMessage(appointment);
            notificationService.notify(appointment.getUser(), message);
        }
    }

    private boolean shouldSendReminder(Appointment appointment) {
        return appointment.isFutureAppointment(clock) && !appointment.isCancelled();
    }

    private String generateReminderMessage(Appointment appointment) {
        return "Reminder: You have an appointment at "
                + appointment.getTimeSlot().getStartTime();
    }
}