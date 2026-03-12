package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.appointment.domain.TimeSlot;
import com.appointment.repository.TimeSlotRepository;

class AppointmentServiceTest {

    @Test
    void shouldReturnOnlyAvailableSlots() {
        TimeSlotRepository repository = new TimeSlotRepository();

        TimeSlot slot1 = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        TimeSlot slot2 = new TimeSlot(LocalDateTime.of(2025, 5, 20, 11, 0));
        TimeSlot slot3 = new TimeSlot(LocalDateTime.of(2025, 5, 20, 12, 0));

        slot2.book();

        repository.addTimeSlot(slot1);
        repository.addTimeSlot(slot2);
        repository.addTimeSlot(slot3);

        AppointmentService service = new AppointmentService(repository);

        List<TimeSlot> availableSlots = service.getAvailableSlots();

        assertEquals(2, availableSlots.size());
        assertTrue(availableSlots.contains(slot1));
        assertTrue(availableSlots.contains(slot3));
    }

    @Test
    void shouldReturnEmptyListWhenAllSlotsAreBooked() {
        TimeSlotRepository repository = new TimeSlotRepository();

        TimeSlot slot1 = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        TimeSlot slot2 = new TimeSlot(LocalDateTime.of(2025, 5, 20, 11, 0));

        slot1.book();
        slot2.book();

        repository.addTimeSlot(slot1);
        repository.addTimeSlot(slot2);

        AppointmentService service = new AppointmentService(repository);

        List<TimeSlot> availableSlots = service.getAvailableSlots();

        assertTrue(availableSlots.isEmpty());
    }
}