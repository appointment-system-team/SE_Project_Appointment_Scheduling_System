package com.appointment.service;

import com.appointment.domain.Appointment;

public class NotificationObserver implements AppointmentObserver {

    private final NotificationService notificationService;

    public NotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void update(Appointment appointment, String eventType) {
        String message = buildMessage(appointment, eventType);
        notificationService.notify(appointment.getUser(), message);
    }

    private String buildMessage(Appointment appointment, String eventType) {
        return "Appointment " + eventType + ": "
                + appointment.getTimeSlot().getStartTime();
    }
}