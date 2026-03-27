package com.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.AppointmentStatus;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.repository.AdminRepository;
import com.appointment.repository.AppointmentRepository;
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
    void shouldCancelFutureAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot slot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        Appointment appointment = new Appointment(
                user,
                slot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        slot.book();
        appointmentRepository.save(appointment);

        service.cancelAppointment(appointment);

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        assertTrue(!slot.isBooked());
    }

    @Test
    void shouldModifyFutureAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot oldSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        TimeSlot newSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(2));
        Appointment appointment = new Appointment(
                user,
                oldSlot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        oldSlot.book();
        appointmentRepository.save(appointment);

        service.modifyAppointment(appointment, newSlot, 90, 3);

        assertEquals(newSlot, appointment.getTimeSlot());
        assertEquals(90, appointment.getDurationInMinutes());
        assertEquals(3, appointment.getParticipantCount());
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
        assertTrue(!oldSlot.isBooked());
        assertTrue(newSlot.isBooked());
    }

    @Test
    void shouldThrowExceptionWhenCancellingWithoutAdminLogin() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot slot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        Appointment appointment = new Appointment(
                user,
                slot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        slot.book();
        appointmentRepository.save(appointment);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.cancelAppointment(appointment));

        assertEquals(
                "Admin login is required for appointment management.",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCancellingPastAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot slot = new TimeSlot(LocalDateTime.now(fixedClock).minusDays(1));
        Appointment appointment = new Appointment(
                user,
                slot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        appointmentRepository.save(appointment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelAppointment(appointment));

        assertEquals("Only future appointments can be modified or cancelled.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAppointmentIsNullDuringCancellation() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelAppointment(null));

        assertEquals("Appointment cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAppointmentDoesNotExist() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot slot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        Appointment appointment = new Appointment(
                user,
                slot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.cancelAppointment(appointment));

        assertEquals("Appointment does not exist.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenModifyingCancelledAppointment() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot oldSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        TimeSlot newSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(2));
        Appointment appointment = new Appointment(
                user,
                oldSlot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        appointment.cancel();
        appointmentRepository.save(appointment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.modifyAppointment(appointment, newSlot, 90, 3));

        assertEquals("Cancelled appointment cannot be modified or cancelled again.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNewSlotIsNull() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot oldSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        Appointment appointment = new Appointment(
                user,
                oldSlot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        oldSlot.book();
        appointmentRepository.save(appointment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.modifyAppointment(appointment, null, 90, 3));

        assertEquals("New time slot cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNewSlotIsAlreadyBooked() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot oldSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        TimeSlot newSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(2));
        Appointment appointment = new Appointment(
                user,
                oldSlot,
                60,
                2,
                AppointmentPurpose.ASSESSMENT,
                AppointmentCategory.GROUP,
                AppointmentMode.IN_PERSON);

        oldSlot.book();
        newSlot.book();
        appointmentRepository.save(appointment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.modifyAppointment(appointment, newSlot, 90, 3));

        assertEquals("New time slot is already booked.", exception.getMessage());
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

    @Test
    void shouldBookAppointmentWhenAllInputsAreValid() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        timeSlotRepository.addTimeSlot(slot);

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        assertEquals(user, appointment.getUser());
        assertEquals(slot, appointment.getTimeSlot());
        assertEquals(60, appointment.getDurationInMinutes());
        assertEquals(1, appointment.getParticipantCount());
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
        assertEquals(AppointmentPurpose.FOLLOW_UP, appointment.getAppointmentPurpose());
        assertEquals(AppointmentCategory.INDIVIDUAL, appointment.getAppointmentCategory());
        assertEquals(AppointmentMode.VIRTUAL, appointment.getAppointmentMode());
        assertTrue(slot.isBooked());
        assertEquals(1, appointmentRepository.findAll().size());
    }

    @Test
    void shouldThrowExceptionWhenBookingWithoutAppointmentRepository() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("AppointmentRepository is required for booking.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        null,
                        slot,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("User cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotIsNull() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        null,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Time slot cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAppointmentPurposeIsNull() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        1,
                        null,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Appointment purpose cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAppointmentCategoryIsNull() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        null,
                        AppointmentMode.VIRTUAL));

        assertEquals("Appointment category cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAppointmentModeIsNull() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        null));

        assertEquals("Appointment mode cannot be null.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTimeSlotIsAlreadyBooked() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        slot.book();

        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Time slot is already booked.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        0,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Invalid duration. Maximum allowed duration is 120 minutes.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDurationExceedsMaximum() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        121,
                        1,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Invalid duration. Maximum allowed duration is 120 minutes.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenParticipantCountIsZero() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        0,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Invalid participant count. Maximum allowed participants is 5.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenParticipantCountExceedsMaximum() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        6,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.GROUP,
                        AppointmentMode.VIRTUAL));

        assertEquals("Invalid participant count. Maximum allowed participants is 5.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUrgentAppointmentViolatesPurposeRule() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        45,
                        1,
                        AppointmentPurpose.URGENT,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Appointment does not satisfy the selected appointment purpose rule.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenFollowUpAppointmentViolatesPurposeRule() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        2,
                        AppointmentPurpose.FOLLOW_UP,
                        AppointmentCategory.GROUP,
                        AppointmentMode.VIRTUAL));

        assertEquals("Appointment does not satisfy the selected appointment purpose rule.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAssessmentAppointmentViolatesPurposeRule() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        1,
                        AppointmentPurpose.ASSESSMENT,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.VIRTUAL));

        assertEquals("Appointment does not satisfy the selected appointment purpose rule.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIndividualAppointmentViolatesCategoryRule() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        2,
                        AppointmentPurpose.ASSESSMENT,
                        AppointmentCategory.INDIVIDUAL,
                        AppointmentMode.IN_PERSON));

        assertEquals("Appointment does not satisfy the selected appointment category rule.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenGroupAppointmentViolatesCategoryRule() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.bookAppointment(
                        user,
                        slot,
                        60,
                        1,
                        AppointmentPurpose.ASSESSMENT,
                        AppointmentCategory.GROUP,
                        AppointmentMode.IN_PERSON));

        assertEquals("Appointment does not satisfy the selected appointment category rule.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenModifiedAppointmentViolatesPurposeRule() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot oldSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        TimeSlot newSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(2));
        Appointment appointment = new Appointment(
                user,
                oldSlot,
                20,
                1,
                AppointmentPurpose.URGENT,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        oldSlot.book();
        appointmentRepository.save(appointment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.modifyAppointment(appointment, newSlot, 45, 1));

        assertEquals("Appointment does not satisfy the selected appointment purpose rule.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenModifiedAppointmentViolatesCategoryRule() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        AuthenticationService authenticationService =
                new AuthenticationService(new AdminRepository());
        authenticationService.login("admin", "1234");

        Clock fixedClock = Clock.fixed(
                Instant.parse("2025-01-01T10:00:00Z"),
                ZoneId.systemDefault());

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authenticationService,
                fixedClock);

        User user = new User("Zeina");
        TimeSlot oldSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(1));
        TimeSlot newSlot = new TimeSlot(LocalDateTime.now(fixedClock).plusDays(2));
        Appointment appointment = new Appointment(
                user,
                oldSlot,
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        oldSlot.book();
        appointmentRepository.save(appointment);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.modifyAppointment(appointment, newSlot, 60, 2));

        assertEquals("Appointment does not satisfy the selected appointment category rule.", exception.getMessage());
    }

    @Test
    void shouldNotifyObserverWhenAppointmentIsBooked() {
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        TimeSlot slot = new TimeSlot(LocalDateTime.of(2025, 5, 20, 10, 0));
        User user = new User("Zeina");

        timeSlotRepository.addTimeSlot(slot);

        AppointmentService service = new AppointmentService(timeSlotRepository, appointmentRepository);

        AppointmentObserver observer = mock(AppointmentObserver.class);
        service.addObserver(observer);

        Appointment appointment = service.bookAppointment(
                user,
                slot,
                60,
                1,
                AppointmentPurpose.FOLLOW_UP,
                AppointmentCategory.INDIVIDUAL,
                AppointmentMode.VIRTUAL);

        verify(observer, times(1)).update(appointment, "BOOKED");
    }
}