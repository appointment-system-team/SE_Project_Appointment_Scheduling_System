package com.appointment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.appointment.domain.TimeSlot;

class TimeSlotRepositoryTest {

    @Test
    void shouldAddTimeSlotAndReturnItInFindAll() {
        TimeSlotRepository repository = new TimeSlotRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        repository.addTimeSlot(slot);

        List<TimeSlot> result = repository.findAll();

        assertEquals(1, result.size());
        assertTrue(result.contains(slot));
    }

    @Test
    void shouldReturnDefensiveCopyFromFindAll() {
        TimeSlotRepository repository = new TimeSlotRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        repository.addTimeSlot(slot);

        List<TimeSlot> result1 = repository.findAll();
        List<TimeSlot> result2 = repository.findAll();

        assertNotSame(result1, result2);

        result1.clear();

        assertEquals(1, repository.findAll().size());
    }
}