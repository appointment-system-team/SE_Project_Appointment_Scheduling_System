package com.appointment.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.TimeSlotRepository;

public class AppointmentService {

    private static final String PURPOSE_RULE_VIOLATION_MESSAGE =
            "Appointment does not satisfy the selected appointment purpose rule.";
    private static final String CATEGORY_RULE_VIOLATION_MESSAGE =
            "Appointment does not satisfy the selected appointment category rule.";
    private static final String MODE_RULE_VIOLATION_MESSAGE =
            "Appointment does not satisfy the selected appointment mode rule.";
    private static final String WORKING_HOURS_RULE_VIOLATION_MESSAGE =
            "Appointment must be within working hours (8:00 AM - 4:00 PM).";
    private static final String WORKING_DAYS_RULE_VIOLATION_MESSAGE =
            "Appointments are not allowed on Friday or Saturday.";
    private static final String APPOINTMENT_REPOSITORY_REQUIRED_FOR_BOOKING_MESSAGE =
            "AppointmentRepository is required for booking.";
    private static final String APPOINTMENT_REPOSITORY_REQUIRED_FOR_MANAGEMENT_MESSAGE =
            "AppointmentRepository is required for appointment management.";
    private static final String ADMIN_LOGIN_REQUIRED_MESSAGE =
            "Admin login is required for appointment management.";

    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final DurationRule durationRule;
    private final ParticipantLimitRule participantLimitRule;
    private final AppointmentPurposeRuleFactory appointmentPurposeRuleFactory;
    private final AppointmentCategoryRuleFactory appointmentCategoryRuleFactory;
    private final AppointmentModeRuleFactory appointmentModeRuleFactory;
    private final WorkingHoursRule workingHoursRule;
    private final WorkingDaysRule workingDaysRule;
    private final AuthenticationService authenticationService;
    private final Clock clock;
    private final List<AppointmentObserver> observers;

    public AppointmentService(TimeSlotRepository timeSlotRepository) {
        this(timeSlotRepository, null, null, Clock.systemDefaultZone());
    }

    public AppointmentService(TimeSlotRepository timeSlotRepository, AppointmentRepository appointmentRepository) {
        this(timeSlotRepository, appointmentRepository, null, Clock.systemDefaultZone());
    }

    public AppointmentService(
            TimeSlotRepository timeSlotRepository,
            AppointmentRepository appointmentRepository,
            AuthenticationService authenticationService) {
        this(timeSlotRepository, appointmentRepository, authenticationService, Clock.systemDefaultZone());
    }

    public AppointmentService(
            TimeSlotRepository timeSlotRepository,
            AppointmentRepository appointmentRepository,
            AuthenticationService authenticationService,
            Clock clock) {

        this.timeSlotRepository = timeSlotRepository;
        this.appointmentRepository = appointmentRepository;
        this.authenticationService = authenticationService;
        this.clock = clock;
        this.durationRule = new DurationRule();
        this.participantLimitRule = new ParticipantLimitRule();
        this.appointmentPurposeRuleFactory = new AppointmentPurposeRuleFactory();
        this.appointmentCategoryRuleFactory = new AppointmentCategoryRuleFactory();
        this.appointmentModeRuleFactory = new AppointmentModeRuleFactory();
        this.workingHoursRule = new WorkingHoursRule();
        this.workingDaysRule = new WorkingDaysRule();
        this.observers = new ArrayList<>();
    }

    public void addObserver(AppointmentObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void removeObserver(AppointmentObserver observer) {
        observers.remove(observer);
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

    public Appointment bookAppointment(
            User user,
            TimeSlot timeSlot,
            int durationInMinutes,
            int participantCount,
            AppointmentPurpose appointmentPurpose,
            AppointmentCategory appointmentCategory,
            AppointmentMode appointmentMode) {

        validateBookingRequest(
                user,
                timeSlot,
                durationInMinutes,
                participantCount,
                appointmentPurpose,
                appointmentCategory,
                appointmentMode);

        Appointment appointment = new Appointment(
                user,
                timeSlot,
                durationInMinutes,
                participantCount,
                appointmentPurpose,
                appointmentCategory,
                appointmentMode);

        validateAppointmentRules(appointment);
        validateUserHasNoTimeConflict(user, null, appointment);

        appointmentRepository.save(appointment);
        timeSlot.book();
        notifyObservers(appointment, "BOOKED");

        return appointment;
    }

    public void cancelAppointmentByUser(Appointment appointment, User user) {
        validateAppointmentModificationRequest(appointment);

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if (!appointment.getUser().equals(user)) {
            throw new IllegalStateException("User can only cancel their own appointment.");
        }

        appointment.cancel();
        appointment.getTimeSlot().unbook();
        notifyObservers(appointment, "CANCELLED");
    }

    public void cancelAppointmentByAdmin(Appointment appointment) {
        validateAdminAccess();
        validateAppointmentModificationRequest(appointment);

        appointment.cancel();
        appointment.getTimeSlot().unbook();
        notifyObservers(appointment, "CANCELLED");
    }

    public void modifyAppointmentByUser(
            Appointment appointment,
            User user,
            TimeSlot newTimeSlot,
            int newDurationInMinutes,
            int newParticipantCount) {

        validateAppointmentModificationRequest(appointment);

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if (!appointment.getUser().equals(user)) {
            throw new IllegalStateException("User can only modify their own appointment.");
        }

        modifyAppointmentInternal(appointment, newTimeSlot, newDurationInMinutes, newParticipantCount);
    }

    public void modifyAppointmentByAdmin(
            Appointment appointment,
            TimeSlot newTimeSlot,
            int newDurationInMinutes,
            int newParticipantCount) {

        validateAdminAccess();
        validateAppointmentModificationRequest(appointment);

        modifyAppointmentInternal(appointment, newTimeSlot, newDurationInMinutes, newParticipantCount);
    }

    private void modifyAppointmentInternal(
            Appointment appointment,
            TimeSlot newTimeSlot,
            int newDurationInMinutes,
            int newParticipantCount) {

        validateNewTimeSlotForModification(appointment, newTimeSlot);
        validateDuration(newDurationInMinutes);
        validateParticipantCount(newParticipantCount);

        Appointment updatedAppointment = new Appointment(
                appointment.getUser(),
                newTimeSlot,
                newDurationInMinutes,
                newParticipantCount,
                appointment.getAppointmentPurpose(),
                appointment.getAppointmentCategory(),
                appointment.getAppointmentMode());

        validateAppointmentRules(updatedAppointment);
        validateUserHasNoTimeConflict(appointment.getUser(), appointment, updatedAppointment);

        if (appointment.getTimeSlot() != newTimeSlot) {
            appointment.getTimeSlot().unbook();
            newTimeSlot.book();
        }

        appointment.reschedule(newTimeSlot, newDurationInMinutes, newParticipantCount);
        notifyObservers(appointment, "MODIFIED");
    }

    private void notifyObservers(Appointment appointment, String eventType) {
        for (AppointmentObserver observer : observers) {
            observer.update(appointment, eventType);
        }
    }

    private void validateBookingRequest(
            User user,
            TimeSlot timeSlot,
            int durationInMinutes,
            int participantCount,
            AppointmentPurpose appointmentPurpose,
            AppointmentCategory appointmentCategory,
            AppointmentMode appointmentMode) {

        validateAppointmentRepositoryForBooking();

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if (timeSlot == null) {
            throw new IllegalArgumentException("Time slot cannot be null.");
        }

        if (appointmentPurpose == null) {
            throw new IllegalArgumentException("Appointment purpose cannot be null.");
        }

        if (appointmentCategory == null) {
            throw new IllegalArgumentException("Appointment category cannot be null.");
        }

        if (appointmentMode == null) {
            throw new IllegalArgumentException("Appointment mode cannot be null.");
        }

        if (!timeSlotRepository.findAll().contains(timeSlot)) {
            throw new IllegalArgumentException("Time slot does not exist in the repository.");
        }

        if (!timeSlot.getStartTime().isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("Appointment must be booked for a future time.");
        }

        if (timeSlot.isBooked()) {
            throw new IllegalArgumentException("Time slot is already booked.");
        }

        validateDuration(durationInMinutes);
        validateParticipantCount(participantCount);
    }

    private void validateAppointmentModificationRequest(Appointment appointment) {
        validateAppointmentRepositoryForManagement();

        if (appointment == null) {
            throw new IllegalArgumentException("Appointment cannot be null.");
        }

        if (!appointmentRepository.contains(appointment)) {
            throw new IllegalArgumentException("Appointment does not exist.");
        }

        if (!appointment.isFutureAppointment(clock)) {
            throw new IllegalArgumentException("Only future appointments can be modified or cancelled.");
        }

        if (appointment.isCancelled()) {
            throw new IllegalArgumentException("Cancelled appointment cannot be modified or cancelled again.");
        }
    }

    private void validateAppointmentRepositoryForBooking() {
        if (appointmentRepository == null) {
            throw new IllegalStateException(APPOINTMENT_REPOSITORY_REQUIRED_FOR_BOOKING_MESSAGE);
        }
    }

    private void validateAppointmentRepositoryForManagement() {
        if (appointmentRepository == null) {
            throw new IllegalStateException(APPOINTMENT_REPOSITORY_REQUIRED_FOR_MANAGEMENT_MESSAGE);
        }
    }

    private void validateAdminAccess() {
        if (authenticationService == null || !authenticationService.isLoggedIn()) {
            throw new IllegalStateException(ADMIN_LOGIN_REQUIRED_MESSAGE);
        }
    }

    private void validateNewTimeSlotForModification(Appointment appointment, TimeSlot newTimeSlot) {
        if (newTimeSlot == null) {
            throw new IllegalArgumentException("New time slot cannot be null.");
        }

        if (!timeSlotRepository.findAll().contains(newTimeSlot)) {
            throw new IllegalArgumentException("New time slot does not exist in the repository.");
        }

        if (!newTimeSlot.getStartTime().isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("New time slot must be in the future.");
        }

        if (appointment.getTimeSlot() != newTimeSlot && newTimeSlot.isBooked()) {
            throw new IllegalArgumentException("New time slot is already booked.");
        }
    }

    private void validateDuration(int durationInMinutes) {
        if (!durationRule.isValid(durationInMinutes)) {
            throw new IllegalArgumentException(
                    "Invalid duration. Maximum allowed duration is "
                            + durationRule.getMaxDurationInMinutes()
                            + " minutes.");
        }
    }

    private void validateParticipantCount(int participantCount) {
        if (!participantLimitRule.isValid(participantCount)) {
            throw new IllegalArgumentException(
                    "Invalid participant count. Maximum allowed participants is "
                            + participantLimitRule.getMaxParticipants()
                            + ".");
        }
    }

    private void validateAppointmentRules(Appointment appointment) {
        validateAppointmentPurposeRule(appointment);
        validateAppointmentCategoryRule(appointment);
        validateAppointmentModeRule(appointment);
        validateWorkingHoursRule(appointment);
        validateWorkingDaysRule(appointment);
    }

    private void validateAppointmentPurposeRule(Appointment appointment) {
        AppointmentPurposeRule appointmentPurposeRule =
                appointmentPurposeRuleFactory.getRule(appointment.getAppointmentPurpose());

        if (!appointmentPurposeRule.isValid(appointment)) {
            throw new IllegalArgumentException(PURPOSE_RULE_VIOLATION_MESSAGE);
        }
    }

    private void validateAppointmentCategoryRule(Appointment appointment) {
        AppointmentCategoryRule appointmentCategoryRule =
                appointmentCategoryRuleFactory.getRule(appointment.getAppointmentCategory());

        if (!appointmentCategoryRule.isValid(appointment)) {
            throw new IllegalArgumentException(CATEGORY_RULE_VIOLATION_MESSAGE);
        }
    }

    private void validateAppointmentModeRule(Appointment appointment) {
        AppointmentModeRule appointmentModeRule =
                appointmentModeRuleFactory.getRule(appointment.getAppointmentMode());

        if (!appointmentModeRule.isValid(appointment)) {
            throw new IllegalArgumentException(MODE_RULE_VIOLATION_MESSAGE);
        }
    }

    private void validateWorkingHoursRule(Appointment appointment) {
        if (!workingHoursRule.isValid(appointment)) {
            throw new IllegalArgumentException(WORKING_HOURS_RULE_VIOLATION_MESSAGE);
        }
    }

    private void validateWorkingDaysRule(Appointment appointment) {
        if (!workingDaysRule.isValid(appointment)) {
            throw new IllegalArgumentException(WORKING_DAYS_RULE_VIOLATION_MESSAGE);
        }
    }

    private void validateUserHasNoTimeConflict(
            User user,
            Appointment currentAppointment,
            Appointment targetAppointment) {

        if (appointmentRepository.hasOverlappingAppointment(user, currentAppointment, targetAppointment)) {
            throw new IllegalArgumentException(
                    "User already has another appointment that overlaps with this time.");
        }
    }
}