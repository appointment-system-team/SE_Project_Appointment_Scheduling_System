package com.appointment.service;

import java.util.List;

import com.appointment.domain.Appointment;
import com.appointment.repository.AppointmentRepository;

public class ReminderService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    public ReminderService(AppointmentRepository appointmentRepository,
                           NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.notificationService = notificationService;
    }

    public void sendReminders() {

        List<Appointment> appointments = appointmentRepository.findAll();

        for (Appointment appointment : appointments) {

            String message = generateReminderMessage(appointment);

            notificationService.notify(
                    appointment.getUser(),
                    message
            );
        }
    }

    private String generateReminderMessage(Appointment appointment) {

        return "Reminder: You have an appointment at "
                + appointment.getTimeSlot().getStartTime();
    }
}