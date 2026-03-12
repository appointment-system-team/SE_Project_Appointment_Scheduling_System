package com.appointment.repository;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.TimeSlot;

public class TimeSlotRepository {

    private List<TimeSlot> timeSlots = new ArrayList<>();

    public void addTimeSlot(TimeSlot timeSlot) {
        timeSlots.add(timeSlot);
    }

    public List<TimeSlot> findAll() {
        return new ArrayList<>(timeSlots);
    }
}