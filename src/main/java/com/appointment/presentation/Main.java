/*package com.appointment.presentation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.appointment.domain.Appointment;
import com.appointment.domain.AppointmentCategory;
import com.appointment.domain.AppointmentMode;
import com.appointment.domain.AppointmentPurpose;
import com.appointment.domain.TimeSlot;
import com.appointment.domain.User;
import com.appointment.repository.AdminRepository;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.TimeSlotRepository;
import com.appointment.repository.UserRepository;
import com.appointment.service.AccountValidationService;
import com.appointment.service.AppointmentService;
import com.appointment.service.AuthenticationService;
import com.appointment.service.MockNotificationService;
import com.appointment.service.NotificationObserver;
import com.appointment.service.ReminderService;
import com.appointment.service.UserAuthenticationService;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AdminRepository adminRepository = new AdminRepository();
        UserRepository userRepository = new UserRepository();
        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();

        AccountValidationService validationService =
                new AccountValidationService(userRepository, adminRepository);

        AuthenticationService adminAuthService = new AuthenticationService(adminRepository);
        UserAuthenticationService userAuthService = new UserAuthenticationService(userRepository);

        AppointmentService service = new AppointmentService(
                timeSlotRepository,
                appointmentRepository,
                adminAuthService
        );

        MockNotificationService mockService = new MockNotificationService();
        NotificationObserver observer = new NotificationObserver(mockService, adminRepository);
        service.addObserver(observer);

        addSampleTimeSlots(timeSlotRepository);

        boolean running = true;

        while (running) {
            System.out.println("\n=== Appointment Scheduling System ===");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as User");
            System.out.println("3. Register New User");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleAdminLogin(scanner, adminAuthService, service, appointmentRepository, mockService);
                    break;

                case 2:
                    handleUserLogin(scanner, userAuthService, service, appointmentRepository);
                    break;

                case 3:
                    registerNewUser(scanner, userRepository, validationService);
                    break;

                case 4:
                    running = false;
                    System.out.println("Exiting system...");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }

    private static void handleAdminLogin(
            Scanner scanner,
            AuthenticationService adminAuthService,
            AppointmentService service,
            AppointmentRepository appointmentRepository,
            MockNotificationService mockService) {

        System.out.println("\n=== Admin Login ===");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!adminAuthService.login(username, password)) {
            System.out.println("Login failed ❌");
            return;
        }

        System.out.println("Login successful ✅");

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. View available slots");
            System.out.println("2. View all appointments");
            System.out.println("3. Modify appointment");
            System.out.println("4. Cancel appointment");
            System.out.println("5. Send reminders");
            System.out.println("6. Show notifications");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        viewAvailableSlots(service);
                        break;

                    case 2:
                        viewAllAppointments(appointmentRepository);
                        break;

                    case 3:
                        modifyAppointmentByAdmin(scanner, service, appointmentRepository);
                        break;

                    case 4:
                        cancelAppointmentByAdmin(scanner, service, appointmentRepository);
                        break;

                    case 5:
                        ReminderService reminderService =
                                new ReminderService(appointmentRepository, mockService);
                        reminderService.sendReminders();
                        System.out.println("Reminders sent ✅");
                        break;

                    case 6:
                        showNotifications(mockService);
                        break;

                    case 7:
                        adminAuthService.logout();
                        loggedIn = false;
                        System.out.println("Admin logged out.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed ❌: " + e.getMessage());
            }
        }
    }

    private static void handleUserLogin(
            Scanner scanner,
            UserAuthenticationService userAuthService,
            AppointmentService service,
            AppointmentRepository appointmentRepository) {

        System.out.println("\n=== User Login ===");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!userAuthService.login(username, password)) {
            System.out.println("Login failed ❌");
            return;
        }

        User user = userAuthService.getLoggedUser();
        System.out.println("Welcome, " + user.getFullName() + " ✅");

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View available slots");
            System.out.println("2. Book appointment");
            System.out.println("3. View my appointments");
            System.out.println("4. Modify my appointment");
            System.out.println("5. Cancel my appointment");
            System.out.println("6. Logout");
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
                        viewUserAppointments(appointmentRepository, user);
                        break;

                    case 4:
                        modifyAppointmentByUser(scanner, service, appointmentRepository, user);
                        break;

                    case 5:
                        cancelAppointmentByUser(scanner, service, appointmentRepository, user);
                        break;

                    case 6:
                        userAuthService.logout();
                        loggedIn = false;
                        System.out.println("User logged out.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed ❌: " + e.getMessage());
            }
        }
    }

    private static void registerNewUser(
            Scanner scanner,
            UserRepository userRepository,
            AccountValidationService validationService) {

        System.out.println("\n=== Register New User ===");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter phone number: ");
        String phoneNumber = scanner.nextLine();

        User newUser = new User(username, password, fullName, email, phoneNumber);
        userRepository.addUser(newUser, validationService);

        System.out.println("User registered successfully ✅");
    }

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

    private static void viewAllAppointments(AppointmentRepository repository) {
        List<Appointment> appointments = repository.findAll();

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("\nAll Appointments:");
        printAppointments(appointments);
    }

    private static void viewUserAppointments(AppointmentRepository repository, User user) {
        List<Appointment> appointments = repository.findByUser(user);

        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("\nMy Appointments:");
        printAppointments(appointments);
    }

    private static void printAppointments(List<Appointment> appointments) {
        for (int i = 0; i < appointments.size(); i++) {
            Appointment a = appointments.get(i);
            System.out.println((i + 1) + ". "
                    + "User: " + a.getUser().getFullName()
                    + ", Username: " + a.getUser().getUsername()
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
        System.out.println("1. URGENT  2. FOLLOW_UP  3. ASSESSMENT");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                return AppointmentPurpose.URGENT;
            case 2:
                return AppointmentPurpose.FOLLOW_UP;
            case 3:
                return AppointmentPurpose.ASSESSMENT;
            default:
                throw new IllegalArgumentException("Invalid purpose");
        }
    }

    private static AppointmentCategory chooseCategory(Scanner scanner) {
        System.out.println("1. INDIVIDUAL  2. GROUP");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                return AppointmentCategory.INDIVIDUAL;
            case 2:
                return AppointmentCategory.GROUP;
            default:
                throw new IllegalArgumentException("Invalid category");
        }
    }

    private static AppointmentMode chooseMode(Scanner scanner) {
        System.out.println("1. IN_PERSON  2. VIRTUAL");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                return AppointmentMode.IN_PERSON;
            case 2:
                return AppointmentMode.VIRTUAL;
            default:
                throw new IllegalArgumentException("Invalid mode");
        }
    }

    private static void modifyAppointmentByUser(
            Scanner scanner,
            AppointmentService service,
            AppointmentRepository repo,
            User user) {

        List<Appointment> list = repo.findByUser(user);

        if (list.isEmpty()) {
            System.out.println("No appointments.");
            return;
        }

        viewUserAppointments(repo, user);

        System.out.print("Choose appointment: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        Appointment appointment = list.get(idx - 1);

        List<TimeSlot> slots = new ArrayList<>(service.getAvailableSlots());
        if (!slots.contains(appointment.getTimeSlot())) {
            slots.add(appointment.getTimeSlot());
        }

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

    private static void cancelAppointmentByUser(
            Scanner scanner,
            AppointmentService service,
            AppointmentRepository repo,
            User user) {

        List<Appointment> list = repo.findByUser(user);

        if (list.isEmpty()) {
            System.out.println("No appointments.");
            return;
        }

        viewUserAppointments(repo, user);

        System.out.print("Choose appointment: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        service.cancelAppointmentByUser(list.get(idx - 1), user);

        System.out.println("Cancelled successfully ✅");
    }

    private static void modifyAppointmentByAdmin(
            Scanner scanner,
            AppointmentService service,
            AppointmentRepository repo) {

        List<Appointment> list = repo.findAll();

        if (list.isEmpty()) {
            System.out.println("No appointments.");
            return;
        }

        viewAllAppointments(repo);

        System.out.print("Choose appointment: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        Appointment appointment = list.get(idx - 1);

        List<TimeSlot> slots = new ArrayList<>(service.getAvailableSlots());
        if (!slots.contains(appointment.getTimeSlot())) {
            slots.add(appointment.getTimeSlot());
        }

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

        service.modifyAppointmentByAdmin(appointment, slots.get(s - 1), d, p);

        System.out.println("Modified successfully ✅");
    }

    private static void cancelAppointmentByAdmin(
            Scanner scanner,
            AppointmentService service,
            AppointmentRepository repo) {

        List<Appointment> list = repo.findAll();

        if (list.isEmpty()) {
            System.out.println("No appointments.");
            return;
        }

        viewAllAppointments(repo);

        System.out.print("Choose appointment: ");
        int idx = scanner.nextInt();
        scanner.nextLine();

        service.cancelAppointmentByAdmin(list.get(idx - 1));

        System.out.println("Cancelled successfully ✅");
    }
}*/