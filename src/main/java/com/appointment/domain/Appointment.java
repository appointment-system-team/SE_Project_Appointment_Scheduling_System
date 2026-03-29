package com.appointment.domain;

import java.time.Clock;
import java.time.LocalDateTime;

public class Appointment {

    private final User user;
    private TimeSlot timeSlot;
    private int durationInMinutes;
    private int participantCount;
    private AppointmentStatus status;
    private AppointmentPurpose appointmentPurpose;
    private AppointmentCategory appointmentCategory;
    private AppointmentMode appointmentMode;

    public Appointment(
            User user,
            TimeSlot timeSlot,
            int durationInMinutes,
            int participantCount,
            AppointmentPurpose appointmentPurpose,
            AppointmentCategory appointmentCategory,
            AppointmentMode appointmentMode) {

        this.user = user;
        this.timeSlot = timeSlot;
        this.durationInMinutes = durationInMinutes;
        this.participantCount = participantCount;
        this.status = AppointmentStatus.CONFIRMED;
        this.appointmentPurpose = appointmentPurpose;
        this.appointmentCategory = appointmentCategory;
        this.appointmentMode = appointmentMode;
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

    public AppointmentPurpose getAppointmentPurpose() {
        return appointmentPurpose;
    }

    public AppointmentCategory getAppointmentCategory() {
        return appointmentCategory;
    }

    public AppointmentMode getAppointmentMode() {
        return appointmentMode;
    }

    public boolean isFutureAppointment() {
        return isFutureAppointment(Clock.systemDefaultZone());
    }

    public boolean isFutureAppointment(Clock clock) {
        return timeSlot.getStartTime().isAfter(LocalDateTime.now(clock));
    }

    public LocalDateTime getEndTime() {
        return timeSlot.getStartTime().plusMinutes(durationInMinutes);
    }

    public boolean overlapsWith(Appointment other) {
        return this.timeSlot.getStartTime().isBefore(other.getEndTime())
                && this.getEndTime().isAfter(other.getTimeSlot().getStartTime());
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