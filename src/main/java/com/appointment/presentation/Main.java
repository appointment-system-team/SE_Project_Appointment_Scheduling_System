package com.appointment.presentation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import com.appointment.domain.*;
import com.appointment.repository.*;
import com.appointment.service.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ========================
        // Setup
        // ========================
        AdminRepository adminRepository = new AdminRepository();
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        AuthenticationService authService = new AuthenticationService(adminRepository);

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                authService
        );

        // ========================
        // Observer (Notifications)
        // ========================
        MockNotificationService mockService = new MockNotificationService();
        NotificationObserver observer = new NotificationObserver(mockService);
        service.addObserver(observer);

        // ========================
        // Admin Login
        // ========================
        System.out.println("=== Admin Login ===");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!authService.login(username, password)) {
            System.out.println("Login failed ❌");
            scanner.close();
            return;
        }

        System.out.println("Login successful ✅");

        // ========================
        // Create User
        // ========================
        User user = new User("Maryam");

        addSampleTimeSlots(timeSlotRepository);

        boolean running = true;

        while (running) {
            System.out.println("\n=== Appointment Scheduling System ===");
            System.out.println("1. View available slots");
            System.out.println("2. Book appointment");
            System.out.println("3. View my appointments");
            System.out.println("4. Modify appointment");
            System.out.println("5. Cancel appointment");
            System.out.println("6. Send reminders");
            System.out.println("7. Show notifications");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {

                    case 1:
                        viewAvailableSlots(service);
                        break;

                    case 2:
                        bookAppointment(scanner, service, user);
                        break;

                    case 3:
                        viewAppointments(appointmentRepository);
                        break;

                    case 4:
                        modifyAppointment(scanner, service, appointmentRepository, user);
                        break;

                    case 5:
                        cancelAppointment(scanner, service, appointmentRepository, user);
                        break;

                    case 6:
                        ReminderService reminderService =
                                new ReminderService(appointmentRepository, mockService);
                        reminderService.sendReminders();
                        System.out.println("Reminders sent ✅");
                        break;

                    case 7:
                        showNotifications(mockService);
                        break;

                    case 8:
                        running = false;
                        System.out.println("Exiting system...");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println("Operation failed ❌: " + e.getMessage());
            }
        }

        authService.logout();
        System.out.println("Admin logged out.");

        scanner.close();
    }

    // ========================
    // Helper Methods
    // ========================

    private static void showNotifications(MockNotificationService mockService) {
        System.out.println("\n=== Notifications ===");

        if (mockService.getSentMessages().isEmpty()) {
            System.out.println("No notifications yet.");
            return;
        }

        for (String msg : mockService.getSentMessages()) {
            System.out.println(msg);
        }
    }

    private static void addSampleTimeSlots(TimeSlotRepository repository) {
        repository.addTimeSlot(new TimeSlot(nextWorkingDayAt(10, 0)));
        repository.addTimeSlot(new TimeSlot(nextWorkingDayAt(11, 0)));
        repository.addTimeSlot(new TimeSlot(nextWorkingDayAtAfterDays(1, 9, 0)));
        repository.addTimeSlot(new TimeSlot(nextWorkingDayAtAfterDays(1, 10, 0)));
        repository.addTimeSlot(new TimeSlot(nextWorkingDayAtAfterDays(2, 12, 0)));
        repository.addTimeSlot(new TimeSlot(nextWorkingDayAtAfterDays(2, 13, 0)));
    }

    private static LocalDateTime nextWorkingDayAt(int hour, int minute) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);

        while (dateTime.getDayOfWeek() == DayOfWeek.FRIDAY
                || dateTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
            dateTime = dateTime.plusDays(1);
        }

        return dateTime;
    }

    private static LocalDateTime nextWorkingDayAtAfterDays(int extraDays, int hour, int minute) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1 + extraDays)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);

        while (dateTime.getDayOfWeek() == DayOfWeek.FRIDAY
                || dateTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
            dateTime = dateTime.plusDays(1);
        }

        return dateTime;
    }

    private static void viewAvailableSlots(AppointmentService service) {
        List<TimeSlot> slots = service.getAvailableSlots();

        if (slots.isEmpty()) {
            System.out.println("No available slots.");
            return;
        }

        System.out.println("\nAvailable slots:");
        for (int i = 0; i < slots.size(); i++) {
            System.out.println((i + 1) + ". " + slots.get(i).getStartTime());
        }
    }

    private static void viewAppointments(AppointmentRepository repository) {
        List<Appointment> appointments = repository.findAll();

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("\nAppointments:");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment a = appointments.get(i);
            System.out.println((i + 1) + ". "
                    + "User: " + a.getUser().getName()
                    + ", Time: " + a.getTimeSlot().getStartTime()
                    + ", Duration: " + a.getDurationInMinutes()
                    + ", Participants: " + a.getParticipantCount()
                    + ", Purpose: " + a.getAppointmentPurpose()
                    + ", Category: " + a.getAppointmentCategory()
                    + ", Mode: " + a.getAppointmentMode()
                    + ", Status: " + a.getStatus());
        }
    }

    private static void bookAppointment(Scanner scanner, AppointmentService service, User user) {

        List<TimeSlot> slots = service.getAvailableSlots();

        if (slots.isEmpty()) {
            System.out.println("No available slots.");
            return;
        }

        viewAvailableSlots(service);

        System.out.print("Choose slot number: ");
        int slotChoice = scanner.nextInt();
        scanner.nextLine();

        TimeSlot selectedSlot = slots.get(slotChoice - 1);

        System.out.print("Enter duration: ");
        int duration = scanner.nextInt();

        System.out.print("Enter participants: ");
        int participants = scanner.nextInt();
        scanner.nextLine();

        AppointmentPurpose purpose = choosePurpose(scanner);
        AppointmentCategory category = chooseCategory(scanner);
        AppointmentMode mode = chooseMode(scanner);

        Appointment appointment = service.bookAppointment(
                user, selectedSlot, duration, participants, purpose, category, mode
        );

        System.out.println("Appointment booked ✅ at " + appointment.getTimeSlot().getStartTime());
    }

    private static AppointmentPurpose choosePurpose(Scanner scanner) {
        System.out.println("1.URGENT  2.FOLLOW_UP  3.ASSESSMENT");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: return AppointmentPurpose.URGENT;
            case 2: return AppointmentPurpose.FOLLOW_UP;
            case 3: return AppointmentPurpose.ASSESSMENT;
            default: throw new IllegalArgumentException("Invalid purpose");
        }
    }

    private static AppointmentCategory chooseCategory(Scanner scanner) {
        System.out.println("1.INDIVIDUAL  2.GROUP");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: return AppointmentCategory.INDIVIDUAL;
            case 2: return AppointmentCategory.GROUP;
            default: throw new IllegalArgumentException("Invalid category");
        }
    }

    private static AppointmentMode chooseMode(Scanner scanner) {
        System.out.println("1.IN_PERSON  2.VIRTUAL");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: return AppointmentMode.IN_PERSON;
            case 2: return AppointmentMode.VIRTUAL;
            default: throw new IllegalArgumentException("Invalid mode");
        }
    }

    private static void modifyAppointment(Scanner scanner, AppointmentService service,
                                          AppointmentRepository repo, User user) {

        List<Appointment> list = repo.findAll();
        if (list.isEmpty()) {
            System.out.println("No appointments.");
            return;
        }

        viewAppointments(repo);

        System.out.print("Choose appointment: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        Appointment appointment = list.get(idx - 1);

        List<TimeSlot> slots = service.getAvailableSlots();
        slots.add(appointment.getTimeSlot());

        System.out.println("Choose new slot:");
        for (int i = 0; i < slots.size(); i++) {
            System.out.println((i + 1) + ". " + slots.get(i).getStartTime());
        }

        int s = scanner.nextInt();

        System.out.print("New duration: ");
        int d = scanner.nextInt();

        System.out.print("New participants: ");
        int p = scanner.nextInt();
        scanner.nextLine();

        service.modifyAppointmentByUser(appointment, user, slots.get(s - 1), d, p);

        System.out.println("Modified successfully ✅");
    }

    private static void cancelAppointment(Scanner scanner, AppointmentService service,
                                          AppointmentRepository repo, User user) {

        List<Appointment> list = repo.findAll();
        if (list.isEmpty()) {
            System.out.println("No appointments.");
            return;
        }

        viewAppointments(repo);

        System.out.print("Choose appointment: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        service.cancelAppointmentByUser(list.get(idx - 1), user);

        System.out.println("Cancelled successfully ✅");
    }
}