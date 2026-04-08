package com.appointment.domain;

import java.time.LocalDateTime;

/**
 * Represents a time slot that can be booked for an appointment.
 * A time slot stores its start time and booking status.
 */
public class TimeSlot {

    /**
     * The start time of the time slot.
     */
    private final LocalDateTime startTime;

    /**
     * Indicates whether the time slot is booked.
     */
    private boolean booked;

    /**
     * Creates a new time slot with the given start time.
     * The time slot is initially not booked.
     *
     * @param startTime the start time of the time slot
     */
    public TimeSlot(LocalDateTime startTime) {
        this.startTime = startTime;
        this.booked = false;
    }

    /**
     * Returns the start time of the time slot.
     *
     * @return the start time of the time slot
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Checks whether the time slot is booked.
     *
     * @return true if the time slot is booked, otherwise false
     */
    public boolean isBooked() {
        return booked;
    }

    /**
     * Marks the time slot as booked.
     */
    public void book() {
        booked = true;
    }

    /**
     * Marks the time slot as not booked.
     */
    public void unbook() {
        booked = false;
    }
}