package com.appointment.service;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.Appointment;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.TimeSlotRepository;

public class AppointmentService {

    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final DurationRule durationRule;
    private final ParticipantLimitRule participantLimitRule;

    public AppointmentService(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.appointmentRepository = null;
        this.durationRule = new DurationRule();
        this.participantLimitRule = new ParticipantLimitRule();
    }

    public AppointmentService(TimeSlotRepository timeSlotRepository, AppointmentRepository appointmentRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.appointmentRepository = appointmentRepository;
        this.durationRule = new DurationRule();
        this.participantLimitRule = new ParticipantLimitRule();
    }

    public List<TimeSlot> getAvailableSlots() {
        List<TimeSlot> availableSlots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlotRepository.findAll()) {
            if (!timeSlot.isBooked()) {
                availableSlots.add(timeSlot);
            }
        }

        return availableSlots;
    }

    public Appointment bookAppointment(User user, TimeSlot timeSlot, int durationInMinutes, int participantCount) {
        validateBookingRequest(user, timeSlot, durationInMinutes, participantCount);

        Appointment appointment = new Appointment(user, timeSlot, durationInMinutes, participantCount);
        appointmentRepository.save(appointment);
        timeSlot.book();

        return appointment;
    }

    public void cancelAppointment(Appointment appointment) {
        validateAppointmentModificationRequest(appointment);

        appointment.cancel();
        appointment.getTimeSlot().unbook();
    }

    public void modifyAppointment(Appointment appointment, TimeSlot newTimeSlot,
                                  int newDurationInMinutes, int newParticipantCount) {
        validateAppointmentModificationRequest(appointment);

        if (newTimeSlot == null) {
            throw new IllegalArgumentException("New time slot cannot be null.");
        }

        if (newTimeSlot.isBooked()) {
            throw new IllegalArgumentException("New time slot is already booked.");
        }

        if (!durationRule.isValid(newDurationInMinutes)) {
            throw new IllegalArgumentException(
                    "Invalid duration. Maximum allowed duration is "
                            + durationRule.getMaxDurationInMinutes() + " minutes.");
        }

        if (!participantLimitRule.isValid(newParticipantCount)) {
            throw new IllegalArgumentException(
                    "Invalid participant count. Maximum allowed participants is "
                            + participantLimitRule.getMaxParticipants() + ".");
        }

        appointment.getTimeSlot().unbook();
        newTimeSlot.book();
        appointment.reschedule(newTimeSlot, newDurationInMinutes, newParticipantCount);
    }

    private void validateBookingRequest(User user, TimeSlot timeSlot, int durationInMinutes, int participantCount) {
        if (appointmentRepository == null) {
            throw new IllegalStateException("AppointmentRepository is required for booking.");
        }

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if (timeSlot == null) {
            throw new IllegalArgumentException("Time slot cannot be null.");
        }

        if (timeSlot.isBooked()) {
            throw new IllegalArgumentException("Time slot is already booked.");
        }

        if (!durationRule.isValid(durationInMinutes)) {
            throw new IllegalArgumentException(
                    "Invalid duration. Maximum allowed duration is " + durationRule.getMaxDurationInMinutes() + " minutes.");
        }

        if (!participantLimitRule.isValid(participantCount)) {
            throw new IllegalArgumentException(
                    "Invalid participant count. Maximum allowed participants is " + participantLimitRule.getMaxParticipants() + ".");
        }
    }

    private void validateAppointmentModificationRequest(Appointment appointment) {
        if (appointmentRepository == null) {
            throw new IllegalStateException("AppointmentRepository is required for appointment management.");
        }

        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        if (!appointmentRepository.contains(appointment)) {
            throw new IllegalArgumentException("Appointment does not exist.");
        }

        if (!appointment.isFutureAppointment()) {
            throw new IllegalArgumentException("Only future appointments can be modified or cancelled.");
        }

        if (appointment.isCancelled()) {
            throw new IllegalArgumentException("Cancelled appointment cannot be modified or cancelled again.");
        }
    }
}