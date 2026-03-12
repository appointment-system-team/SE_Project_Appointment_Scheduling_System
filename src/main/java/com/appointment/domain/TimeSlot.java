package com.appointment.domain;

import java.time.LocalDateTime;

public class TimeSlot {

    private LocalDateTime startTime;
    private boolean booked;

    public TimeSlot(LocalDateTime startTime) {
        this.startTime = startTime;
        this.booked = false;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public boolean isBooked() {
        return booked;
    }

    public void book() {
        booked = true;
    }
}