package com.appointment.service;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.TimeSlot;
import com.appointment.repository.TimeSlotRepository;

public class AppointmentService {

    private TimeSlotRepository repository;

    public AppointmentService(TimeSlotRepository repository) {
        this.repository = repository;
    }

    public List<TimeSlot> getAvailableSlots() {
        List<TimeSlot> availableSlots = new ArrayList<>();

        for (TimeSlot timeSlot : repository.findAll()) {
            if (!timeSlot.isBooked()) {
                availableSlots.add(timeSlot);
            }
        }

        return availableSlots;
    }
}