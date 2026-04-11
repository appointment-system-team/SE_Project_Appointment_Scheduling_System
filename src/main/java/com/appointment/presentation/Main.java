package com.appointment.presentation;

import com.appointment.domain.Administrator;
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
import com.appointment.service.NotificationService;
import com.appointment.service.ReminderService;
import com.appointment.service.UserAuthenticationService;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.appointment.config.AppConfig;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {

        TimeSlotRepository timeSlotRepository = new TimeSlotRepository();
        AppointmentRepository appointmentRepository = new AppointmentRepository();
        UserRepository userRepository = new UserRepository();
        AdminRepository adminRepository = new AdminRepository();

        AuthenticationService adminAuthService = new AuthenticationService(adminRepository);
        UserAuthenticationService userAuthService = new UserAuthenticationService(userRepository);
        AccountValidationService validationService =
                new AccountValidationService(userRepository, adminRepository);

        NotificationService notificationService = new MockNotificationService();

        AppointmentService appointmentService =
                new AppointmentService(timeSlotRepository, appointmentRepository, adminAuthService);

        ReminderService reminderService =
                new ReminderService(appointmentRepository, notificationService);

        NotificationObserver notificationObserver =
                new NotificationObserver(notificationService, adminRepository);

        appointmentService.addObserver(notificationObserver);

        seedSampleUsers(userRepository, validationService);
        seedSampleWeekSlots(timeSlotRepository);

        startMenu(
                userRepository,
                validationService,
                userAuthService,
                adminAuthService,
                adminRepository,
                appointmentRepository,
                timeSlotRepository,
                appointmentService,
                reminderService
        );
    }

    private static void startMenu(
            UserRepository userRepository,
            AccountValidationService validationService,
            UserAuthenticationService userAuthService,
            AuthenticationService adminAuthService,
            AdminRepository adminRepository,
            AppointmentRepository appointmentRepository,
            TimeSlotRepository timeSlotRepository,
            AppointmentService appointmentService,
            ReminderService reminderService) {

        boolean running = true;

        while (running) {
            System.out.println("\n========== START MENU ==========");
            System.out.println("1. Register User");
            System.out.println("2. Login User");
            System.out.println("3. Login Admin");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            int choice = readInt();

            try {
                switch (choice) {
                    case 1:
                        registerUser(userRepository, validationService);
                        break;
                    case 2:
                        if (loginUser(userAuthService)) {
                            userMenu(
                                    userAuthService,
                                    adminRepository,
                                    appointmentRepository,
                                    timeSlotRepository,
                                    appointmentService
                            );
                        }
                        break;
                    case 3:
                        if (loginAdmin(adminAuthService)) {
                            adminMenu(
                                    adminAuthService,
                                    adminRepository,
                                    appointmentRepository,
                                    timeSlotRepository,
                                    appointmentService,
                                    reminderService
                            );
                        }
                        break;
                    case 0:
                        running = false;
                        System.out.println("Goodbye.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void userMenu(
            UserAuthenticationService userAuthService,
            AdminRepository adminRepository,
            AppointmentRepository appointmentRepository,
            TimeSlotRepository timeSlotRepository,
            AppointmentService appointmentService) {

        boolean running = true;

        while (running) {
            System.out.println("\n========== USER MENU ==========");
            System.out.println("1. View Available Slots");
            System.out.println("2. Book Appointment");
            System.out.println("3. View My Appointments");
            System.out.println("4. Cancel My Appointment");
            System.out.println("5. Modify My Appointment");
            System.out.println("6. Logout");
            System.out.print("Choose: ");

            int choice = readInt();

            try {
                switch (choice) {
                    case 1:
                        openSlotsViewerGui(
                                getAllSlots(timeSlotRepository),
                                appointmentRepository.findAll()
                        );
                        break;
                    case 2:
                        bookAppointmentWithGui(
                                appointmentService,
                                userAuthService,
                                adminRepository,
                                appointmentRepository,
                                timeSlotRepository
                        );
                        break;
                    case 3:
                        viewMyAppointments(appointmentRepository, userAuthService);
                        break;
                    case 4:
                        cancelMyAppointment(
                                appointmentService,
                                appointmentRepository,
                                userAuthService,
                                adminRepository
                        );
                        break;
                    case 5:
                        modifyMyAppointmentWithGui(
                                appointmentService,
                                appointmentRepository,
                                timeSlotRepository,
                                userAuthService,
                                adminRepository
                        );
                        break;
                    case 6:
                        userAuthService.logout();
                        running = false;
                        System.out.println("User logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void adminMenu(
            AuthenticationService adminAuthService,
            AdminRepository adminRepository,
            AppointmentRepository appointmentRepository,
            TimeSlotRepository timeSlotRepository,
            AppointmentService appointmentService,
            ReminderService reminderService) {

        boolean running = true;

        while (running) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View All Appointments");
            System.out.println("2. Cancel Appointment");
            System.out.println("3. Modify Appointment");
            System.out.println("4. Send Reminders");
            System.out.println("5. Logout");
            System.out.print("Choose: ");

            int choice = readInt();

            try {
                switch (choice) {
                    case 1:
                        openAppointmentsViewerGui(appointmentRepository.findAll());
                        break;
                    case 2:
                        adminCancelAppointment(
                                appointmentService,
                                appointmentRepository,
                                adminAuthService
                        );
                        break;
                    case 3:
                        adminModifyAppointmentWithGui(
                                appointmentService,
                                appointmentRepository,
                                timeSlotRepository,
                                adminAuthService
                        );
                        break;
                    case 4:
                        reminderService.sendReminders();
                        sendReminderEmailsToUsers(appointmentRepository.findAll());
                        System.out.println("Reminders sent.");
                        break;
                    case 5:
                        adminAuthService.logout();
                        running = false;
                        System.out.println("Admin logged out.");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void registerUser(UserRepository userRepository, AccountValidationService validationService) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Phone number: ");
        String phoneNumber = scanner.nextLine().trim();

        User user = new User(username, password, fullName, email, phoneNumber);
        userRepository.addUser(user, validationService);

        System.out.println("User registered successfully.");
    }

    private static boolean loginUser(UserAuthenticationService userAuthService) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        boolean success = userAuthService.login(username, password);
        System.out.println(success ? "User login successful." : "Invalid user credentials.");
        return success;
    }

    private static boolean loginAdmin(AuthenticationService adminAuthService) {
        System.out.print("Admin username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        boolean success = adminAuthService.login(username, password);
        System.out.println(success ? "Admin login successful." : "Invalid admin credentials.");
        return success;
    }

    private static void openSlotsViewerGui(
            List<TimeSlot> allSlots,
            List<Appointment> appointments) {

        if (allSlots.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No slots found.");
            return;
        }

        JFrame frame = new JFrame("Weekly Slots");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(0, 5, 10, 10));

        Map<LocalDate, List<TimeSlot>> grouped =
                allSlots.stream().collect(Collectors.groupingBy(
                        slot -> slot.getStartTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        for (Map.Entry<LocalDate, List<TimeSlot>> entry : grouped.entrySet()) {
            JPanel dayPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            dayPanel.setBorder(new TitledBorder(entry.getKey().format(DATE_FORMAT)));

            List<TimeSlot> daySlots = entry.getValue().stream()
                    .sorted(Comparator.comparing(TimeSlot::getStartTime))
                    .collect(Collectors.toList());

            for (TimeSlot slot : daySlots) {
                JButton button = new JButton(slot.getStartTime().toLocalTime().format(TIME_FORMAT));
                button.setEnabled(false);
                button.setOpaque(true);

                if (isSlotOccupiedByAnyAppointment(slot.getStartTime(), appointments, null)) {
                    button.setBackground(new Color(244, 204, 204));
                } else {
                    button.setBackground(new Color(198, 239, 206));
                }

                dayPanel.add(button);
            }

            mainPanel.add(dayPanel);
        }

        frame.add(new JScrollPane(mainPanel));
        frame.setVisible(true);
    }

    private static void openAppointmentsViewerGui(List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No appointments found.");
            return;
        }

        JFrame frame = new JFrame("Appointments");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1100, 600);
        frame.setLocationRelativeTo(null);

        String[] columns = {
                "User", "Start Time", "Duration", "Participants", "Purpose", "Category", "Mode", "Status"
        };

        String[][] data = new String[appointments.size()][8];

        for (int i = 0; i < appointments.size(); i++) {
            Appointment a = appointments.get(i);
            data[i][0] = a.getUser().getFullName();
            data[i][1] = a.getTimeSlot().getStartTime().toString();
            data[i][2] = String.valueOf(a.getDurationInMinutes());
            data[i][3] = String.valueOf(a.getParticipantCount());
            data[i][4] = String.valueOf(a.getAppointmentPurpose());
            data[i][5] = String.valueOf(a.getAppointmentCategory());
            data[i][6] = String.valueOf(a.getAppointmentMode());
            data[i][7] = String.valueOf(a.getStatus());
        }

        JTable table = new JTable(data, columns);
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    private static void bookAppointmentWithGui(
            AppointmentService appointmentService,
            UserAuthenticationService userAuthService,
            AdminRepository adminRepository,
            AppointmentRepository appointmentRepository,
            TimeSlotRepository timeSlotRepository) {

        User currentUser = userAuthService.getLoggedUser();
        if (currentUser == null) {
            System.out.println("Please login as user first.");
            return;
        }

        TimeSlot selectedSlot = pickSlotFromGui(
                getAllSlots(timeSlotRepository),
                appointmentRepository.findAll(),
                null,
                "Book Appointment - Select a Slot"
        );

        if (selectedSlot == null) {
            return;
        }

        Integer duration = askIntegerDialog("Enter duration in minutes:");
        if (duration == null) {
            return;
        }

        Integer participantCount = askIntegerDialog("Enter participant count:");
        if (participantCount == null) {
            return;
        }

        AppointmentPurpose purpose = choosePurposeDialog();
        if (purpose == null) {
            return;
        }

        AppointmentCategory category = chooseCategoryDialog();
        if (category == null) {
            return;
        }

        AppointmentMode mode = chooseModeDialog();
        if (mode == null) {
            return;
        }

        Appointment appointment = appointmentService.bookAppointment(
                currentUser,
                selectedSlot,
                duration,
                participantCount,
                purpose,
                category,
                mode
        );

        JOptionPane.showMessageDialog(
                null,
                "Appointment booked successfully at " + appointment.getTimeSlot().getStartTime()
        );

        sendEmail(
                currentUser.getEmail(),
                "Appointment Confirmed",
                "Your appointment has been booked successfully for "
                        + appointment.getTimeSlot().getStartTime() + "."
        );

        for (Administrator admin : adminRepository.findAll()) {
            sendEmail(
                    admin.getEmail(),
                    "New Appointment Booked",
                    "User " + currentUser.getFullName()
                            + " booked an appointment for "
                            + appointment.getTimeSlot().getStartTime() + "."
            );
        }
    }

    private static void viewMyAppointments(
            AppointmentRepository appointmentRepository,
            UserAuthenticationService userAuthService) {

        User currentUser = userAuthService.getLoggedUser();
        if (currentUser == null) {
            System.out.println("Please login as user first.");
            return;
        }

        List<Appointment> myAppointments = appointmentRepository.findByUser(currentUser);

        if (myAppointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        System.out.println("\nMy Appointments:");
        for (int i = 0; i < myAppointments.size(); i++) {
            printAppointment(i, myAppointments.get(i));
        }
    }

    private static void cancelMyAppointment(
            AppointmentService appointmentService,
            AppointmentRepository appointmentRepository,
            UserAuthenticationService userAuthService,
            AdminRepository adminRepository) {

        User currentUser = userAuthService.getLoggedUser();
        if (currentUser == null) {
            System.out.println("Please login as user first.");
            return;
        }

        List<Appointment> myAppointments = appointmentRepository.findByUser(currentUser);

        if (myAppointments.isEmpty()) {
            System.out.println("No appointments to cancel.");
            return;
        }

        for (int i = 0; i < myAppointments.size(); i++) {
            printAppointment(i, myAppointments.get(i));
        }

        System.out.print("Choose appointment index to cancel: ");
        int index = readInt();

        if (index < 0 || index >= myAppointments.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Appointment appointment = myAppointments.get(index);
        String appointmentTime = appointment.getTimeSlot().getStartTime().toString();

        appointmentService.cancelAppointmentByUser(appointment, currentUser);
        System.out.println("Appointment cancelled successfully.");

        sendEmail(
                currentUser.getEmail(),
                "Appointment Cancelled",
                "Your appointment on " + appointmentTime + " has been cancelled successfully."
        );

        for (Administrator admin : adminRepository.findAll()) {
            sendEmail(
                    admin.getEmail(),
                    "Appointment Cancelled by User",
                    "User " + currentUser.getFullName()
                            + " cancelled the appointment on " + appointmentTime + "."
            );
        }
    }

    private static void modifyMyAppointmentWithGui(
            AppointmentService appointmentService,
            AppointmentRepository appointmentRepository,
            TimeSlotRepository timeSlotRepository,
            UserAuthenticationService userAuthService,
            AdminRepository adminRepository) {

        User currentUser = userAuthService.getLoggedUser();
        if (currentUser == null) {
            System.out.println("Please login as user first.");
            return;
        }

        List<Appointment> myAppointments = appointmentRepository.findByUser(currentUser);
        if (myAppointments.isEmpty()) {
            System.out.println("No appointments to modify.");
            return;
        }

        for (int i = 0; i < myAppointments.size(); i++) {
            printAppointment(i, myAppointments.get(i));
        }

        System.out.print("Choose appointment index to modify: ");
        int appointmentIndex = readInt();

        if (appointmentIndex < 0 || appointmentIndex >= myAppointments.size()) {
            System.out.println("Invalid appointment index.");
            return;
        }

        Appointment currentAppointment = myAppointments.get(appointmentIndex);

        TimeSlot newSlot = pickSlotFromGui(
                getAllSlots(timeSlotRepository),
                appointmentRepository.findAll(),
                currentAppointment,
                "Modify Appointment - Select New Slot"
        );

        if (newSlot == null) {
            return;
        }

        Integer newDuration = askIntegerDialog("Enter new duration in minutes:");
        if (newDuration == null) {
            return;
        }

        Integer newParticipantCount = askIntegerDialog("Enter new participant count:");
        if (newParticipantCount == null) {
            return;
        }

        appointmentService.modifyAppointmentByUser(
                currentAppointment,
                currentUser,
                newSlot,
                newDuration,
                newParticipantCount
        );

        JOptionPane.showMessageDialog(null, "Appointment modified successfully.");

        sendEmail(
                currentUser.getEmail(),
                "Appointment Modified",
                "Your appointment has been modified successfully. "
                        + "New time: " + currentAppointment.getTimeSlot().getStartTime() + "."
        );

        for (Administrator admin : adminRepository.findAll()) {
            sendEmail(
                    admin.getEmail(),
                    "Appointment Modified by User",
                    "User " + currentUser.getFullName()
                            + " modified their appointment. New time: "
                            + currentAppointment.getTimeSlot().getStartTime() + "."
            );
        }
    }

    private static void adminCancelAppointment(
            AppointmentService appointmentService,
            AppointmentRepository appointmentRepository,
            AuthenticationService adminAuthService) {

        if (!adminAuthService.isLoggedIn()) {
            System.out.println("Please login as admin first.");
            return;
        }

        List<Appointment> appointments = appointmentRepository.findAll();
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (int i = 0; i < appointments.size(); i++) {
            printAppointment(i, appointments.get(i));
        }

        System.out.print("Choose appointment index to cancel: ");
        int index = readInt();

        if (index < 0 || index >= appointments.size()) {
            System.out.println("Invalid index.");
            return;
        }

        Appointment appointment = appointments.get(index);
        String userEmail = appointment.getUser().getEmail();
        String appointmentTime = appointment.getTimeSlot().getStartTime().toString();

        appointmentService.cancelAppointmentByAdmin(appointment);
        System.out.println("Appointment cancelled by admin.");

        sendEmail(
                userEmail,
                "Appointment Cancelled by Admin",
                "Your appointment on " + appointmentTime + " was cancelled by the administrator."
        );
    }

    private static void adminModifyAppointmentWithGui(
            AppointmentService appointmentService,
            AppointmentRepository appointmentRepository,
            TimeSlotRepository timeSlotRepository,
            AuthenticationService adminAuthService) {

        if (!adminAuthService.isLoggedIn()) {
            System.out.println("Please login as admin first.");
            return;
        }

        List<Appointment> appointments = appointmentRepository.findAll();
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        for (int i = 0; i < appointments.size(); i++) {
            printAppointment(i, appointments.get(i));
        }

        System.out.print("Choose appointment index to modify: ");
        int appointmentIndex = readInt();

        if (appointmentIndex < 0 || appointmentIndex >= appointments.size()) {
            System.out.println("Invalid appointment index.");
            return;
        }

        Appointment currentAppointment = appointments.get(appointmentIndex);
        String userEmail = currentAppointment.getUser().getEmail();

        TimeSlot newSlot = pickSlotFromGui(
                getAllSlots(timeSlotRepository),
                appointmentRepository.findAll(),
                currentAppointment,
                "Admin Modify Appointment - Select New Slot"
        );

        if (newSlot == null) {
            return;
        }

        Integer newDuration = askIntegerDialog("Enter new duration in minutes:");
        if (newDuration == null) {
            return;
        }

        Integer newParticipantCount = askIntegerDialog("Enter new participant count:");
        if (newParticipantCount == null) {
            return;
        }

        appointmentService.modifyAppointmentByAdmin(
                currentAppointment,
                newSlot,
                newDuration,
                newParticipantCount
        );

        JOptionPane.showMessageDialog(null, "Appointment modified by admin.");

        sendEmail(
                userEmail,
                "Appointment Modified by Admin",
                "Your appointment was modified by the administrator. "
                        + "The updated time is " + currentAppointment.getTimeSlot().getStartTime() + "."
        );
    }

    private static TimeSlot pickSlotFromGui(
            List<TimeSlot> allSlots,
            List<Appointment> appointments,
            Appointment ignoredAppointment,
            String title) {

        if (allSlots == null || allSlots.isEmpty()) {
            return null;
        }

        final TimeSlot[] selectedSlot = {null};

        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setSize(1200, 700);
        dialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridLayout(0, 5, 10, 10));

        Map<LocalDate, List<TimeSlot>> grouped =
                allSlots.stream().collect(Collectors.groupingBy(
                        slot -> slot.getStartTime().toLocalDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        for (Map.Entry<LocalDate, List<TimeSlot>> entry : grouped.entrySet()) {
            JPanel dayPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            dayPanel.setBorder(new TitledBorder(entry.getKey().format(DATE_FORMAT)));

            List<TimeSlot> daySlots = entry.getValue().stream()
                    .sorted(Comparator.comparing(TimeSlot::getStartTime))
                    .collect(Collectors.toList());

            for (TimeSlot slot : daySlots) {
                String label = slot.getStartTime().toLocalTime().format(TIME_FORMAT);
                JButton button = new JButton(label);
                button.setOpaque(true);

                if (isSlotOccupiedByAnyAppointment(slot.getStartTime(), appointments, ignoredAppointment)) {
                    button.setBackground(new Color(244, 204, 204));
                    button.setEnabled(false);
                } else {
                    button.setBackground(new Color(198, 239, 206));
                    button.addActionListener(e -> {
                        selectedSlot[0] = slot;
                        dialog.dispose();
                    });
                }

                dayPanel.add(button);
            }

            mainPanel.add(dayPanel);
        }

        dialog.add(new JScrollPane(mainPanel));
        dialog.setVisible(true);

        return selectedSlot[0];
    }

    private static boolean isSlotOccupiedByAnyAppointment(
            LocalDateTime slotTime,
            List<Appointment> appointments,
            Appointment ignoredAppointment) {

        for (Appointment appointment : appointments) {
            if (appointment == ignoredAppointment) {
                continue;
            }

            if (appointment.isCancelled()) {
                continue;
            }

            LocalDateTime start = appointment.getTimeSlot().getStartTime();
            LocalDateTime end = start.plusMinutes(appointment.getDurationInMinutes());

            if (!slotTime.isBefore(start) && slotTime.isBefore(end)) {
                return true;
            }
        }

        return false;
    }

    private static Integer askIntegerDialog(String message) {
        String input = JOptionPane.showInputDialog(null, message);
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number.");
            return null;
        }
    }

    private static AppointmentPurpose choosePurposeDialog() {
        Object[] choices = {
            "URGENT - Max 30 minutes (quick appointment)",
            "FOLLOW_UP - Individual appointment only",
            "ASSESSMENT - In-person appointment only"
        };

        Object result = JOptionPane.showInputDialog(
                null,
                "Choose purpose:",
                "Appointment Purpose",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]
        );

        if (result == null) return null;

        String selected = result.toString();

        if (selected.startsWith("URGENT")) return AppointmentPurpose.URGENT;
        if (selected.startsWith("FOLLOW_UP")) return AppointmentPurpose.FOLLOW_UP;
        if (selected.startsWith("ASSESSMENT")) return AppointmentPurpose.ASSESSMENT;

        return null;
    }

    private static AppointmentCategory chooseCategoryDialog() {
        Object[] choices = {"INDIVIDUAL", "GROUP"};
        Object result = JOptionPane.showInputDialog(
                null,
                "Choose category:",
                "Appointment Category",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]
        );

        if (result == null) {
            return null;
        }

        switch (result.toString()) {
            case "INDIVIDUAL":
                return AppointmentCategory.INDIVIDUAL;
            case "GROUP":
                return AppointmentCategory.GROUP;
            default:
                return null;
        }
    }

    private static AppointmentMode chooseModeDialog() {
        Object[] choices = {"VIRTUAL", "IN_PERSON"};
        Object result = JOptionPane.showInputDialog(
                null,
                "Choose mode:",
                "Appointment Mode",
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]
        );

        if (result == null) {
            return null;
        }

        switch (result.toString()) {
            case "VIRTUAL":
                return AppointmentMode.VIRTUAL;
            case "IN_PERSON":
                return AppointmentMode.IN_PERSON;
            default:
                return null;
        }
    }

    private static void printAppointment(int index, Appointment appointment) {
        System.out.println(
                index + ". User: " + appointment.getUser().getFullName()
                        + " | Time: " + appointment.getTimeSlot().getStartTime()
                        + " | Duration: " + appointment.getDurationInMinutes() + " mins"
                        + " | Participants: " + appointment.getParticipantCount()
                        + " | Purpose: " + appointment.getAppointmentPurpose()
                        + " | Category: " + appointment.getAppointmentCategory()
                        + " | Mode: " + appointment.getAppointmentMode()
                        + " | Status: " + appointment.getStatus()
        );
    }

    private static int readInt() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    private static List<TimeSlot> getAllSlots(TimeSlotRepository timeSlotRepository) {
        return timeSlotRepository.findAll().stream()
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .collect(Collectors.toList());
    }

    private static void seedSampleUsers(
            UserRepository userRepository,
            AccountValidationService validationService) {

        try {
            userRepository.addUser(
                    new User("user1", "1234", "Test User One", "user1@gmail.com", "0599000001"),
                    validationService
            );

            userRepository.addUser(
                    new User("user2", "1234", "Test User Two", "user2@gmail.com", "0599000002"),
                    validationService
            );
        } catch (Exception ignored) {
        }
    }

    private static void seedSampleWeekSlots(TimeSlotRepository timeSlotRepository) {
        LocalDate date = LocalDate.now().plusDays(1);
        int workingDaysAdded = 0;

        while (workingDaysAdded < 5) {
            if (date.getDayOfWeek() != DayOfWeek.FRIDAY && date.getDayOfWeek() != DayOfWeek.SATURDAY) {
                for (int hour = 8; hour < 16; hour++) {
                    timeSlotRepository.addTimeSlot(new TimeSlot(LocalDateTime.of(date, LocalTime.of(hour, 0))));
                    timeSlotRepository.addTimeSlot(new TimeSlot(LocalDateTime.of(date, LocalTime.of(hour, 30))));
                }
                workingDaysAdded++;
            }
            date = date.plusDays(1);
        }
    }

    private static void sendReminderEmailsToUsers(List<Appointment> appointments) {
        LocalDateTime now = LocalDateTime.now();

        for (Appointment appointment : appointments) {
            if (appointment.isCancelled()) {
                continue;
            }

            if (appointment.getTimeSlot().getStartTime().isAfter(now)) {
                sendEmail(
                        appointment.getUser().getEmail(),
                        "Appointment Reminder",
                        "Reminder: you have an upcoming appointment at "
                                + appointment.getTimeSlot().getStartTime() + "."
                );
            }
        }
    }

    private static void sendEmail(String to, String subject, String body) {
    	final String senderEmail = AppConfig.getSenderEmail();
    	final String appPassword = AppConfig.getAppPassword();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
    }
}