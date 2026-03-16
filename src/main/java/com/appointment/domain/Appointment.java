package com.appointment.domain;

import java.time.LocalDateTime;

public class Appointment {

    private final User user;
    private TimeSlot timeSlot;
    private int durationInMinutes;
    private int participantCount;
    private AppointmentStatus status;

    public Appointment(User user, TimeSlot timeSlot, int durationInMinutes, int participantCount) {
        this.user = user;
        this.timeSlot = timeSlot;
        this.durationInMinutes = durationInMinutes;
        this.participantCount = participantCount;
        this.status = AppointmentStatus.CONFIRMED;
    }

    public User getUser() {
        return user;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public boolean isFutureAppointment() {
        return timeSlot.getStartTime().isAfter(LocalDateTime.now());
    }

    public boolean isCancelled() {
        return status == AppointmentStatus.CANCELLED;
    }

    public void cancel() {
        status = AppointmentStatus.CANCELLED;
    }

    public void reschedule(TimeSlot newTimeSlot, int newDurationInMinutes, int newParticipantCount) {
        timeSlot = newTimeSlot;
        durationInMinutes = newDurationInMinutes;
        participantCount = newParticipantCount;
        status = AppointmentStatus.CONFIRMED;
    }
}