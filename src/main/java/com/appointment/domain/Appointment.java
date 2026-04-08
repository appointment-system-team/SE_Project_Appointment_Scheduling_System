package com.appointment.domain;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Represents an appointment in the appointment scheduling system.
 * An appointment contains user information, time details, duration,
 * participant count, status, purpose, category, and mode.
 */
public class Appointment {

    /**
     * The user who booked the appointment.
     */
    private final User user;

    /**
     * The time slot assigned to the appointment.
     */
    private TimeSlot timeSlot;

    /**
     * The duration of the appointment in minutes.
     */
    private int durationInMinutes;

    /**
     * The number of participants in the appointment.
     */
    private int participantCount;

    /**
     * The current status of the appointment.
     */
    private AppointmentStatus status;

    /**
     * The purpose of the appointment.
     */
    private AppointmentPurpose appointmentPurpose;

    /**
     * The category of the appointment.
     */
    private AppointmentCategory appointmentCategory;

    /**
     * The mode of the appointment.
     */
    private AppointmentMode appointmentMode;

    /**
     * Creates a new appointment with the given details.
     *
     * @param user the user who booked the appointment
     * @param timeSlot the selected time slot
     * @param durationInMinutes the appointment duration in minutes
     * @param participantCount the number of participants
     * @param appointmentPurpose the purpose of the appointment
     * @param appointmentCategory the category of the appointment
     * @param appointmentMode the mode of the appointment
     */
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

    /**
     * Returns the user of the appointment.
     *
     * @return the user who booked the appointment
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the time slot of the appointment.
     *
     * @return the appointment time slot
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * Returns the duration of the appointment in minutes.
     *
     * @return the appointment duration in minutes
     */
    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    /**
     * Returns the number of participants in the appointment.
     *
     * @return the participant count
     */
    public int getParticipantCount() {
        return participantCount;
    }

    /**
     * Returns the current status of the appointment.
     *
     * @return the appointment status
     */
    public AppointmentStatus getStatus() {
        return status;
    }

    /**
     * Returns the purpose of the appointment.
     *
     * @return the appointment purpose
     */
    public AppointmentPurpose getAppointmentPurpose() {
        return appointmentPurpose;
    }

    /**
     * Returns the category of the appointment.
     *
     * @return the appointment category
     */
    public AppointmentCategory getAppointmentCategory() {
        return appointmentCategory;
    }

    /**
     * Returns the mode of the appointment.
     *
     * @return the appointment mode
     */
    public AppointmentMode getAppointmentMode() {
        return appointmentMode;
    }

    /**
     * Checks whether the appointment is scheduled in the future
     * using the system default clock.
     *
     * @return true if the appointment is in the future, otherwise false
     */
    public boolean isFutureAppointment() {
        return isFutureAppointment(Clock.systemDefaultZone());
    }

    /**
     * Checks whether the appointment is scheduled in the future
     * using the given clock.
     *
     * @param clock the clock used to compare the current time
     * @return true if the appointment is in the future, otherwise false
     */
    public boolean isFutureAppointment(Clock clock) {
        return timeSlot.getStartTime().isAfter(LocalDateTime.now(clock));
    }

    /**
     * Returns the end time of the appointment.
     *
     * @return the calculated end time of the appointment
     */
    public LocalDateTime getEndTime() {
        return timeSlot.getStartTime().plusMinutes(durationInMinutes);
    }

    /**
     * Checks whether this appointment overlaps with another appointment.
     *
     * @param other the other appointment to compare with
     * @return true if the two appointments overlap, otherwise false
     */
    public boolean overlapsWith(Appointment other) {
        return this.timeSlot.getStartTime().isBefore(other.getEndTime())
                && this.getEndTime().isAfter(other.getTimeSlot().getStartTime());
    }

    /**
     * Checks whether the appointment is cancelled.
     *
     * @return true if the appointment status is cancelled, otherwise false
     */
    public boolean isCancelled() {
        return status == AppointmentStatus.CANCELLED;
    }

    /**
     * Cancels the appointment by changing its status to cancelled.
     */
    public void cancel() {
        status = AppointmentStatus.CANCELLED;
    }

    /**
     * Reschedules the appointment with a new time slot, duration,
     * and participant count.
     *
     * @param newTimeSlot the new time slot of the appointment
     * @param newDurationInMinutes the new duration in minutes
     * @param newParticipantCount the new participant count
     */
    public void reschedule(TimeSlot newTimeSlot, int newDurationInMinutes, int newParticipantCount) {
        timeSlot = newTimeSlot;
        durationInMinutes = newDurationInMinutes;
        participantCount = newParticipantCount;
        status = AppointmentStatus.CONFIRMED;
    }
}