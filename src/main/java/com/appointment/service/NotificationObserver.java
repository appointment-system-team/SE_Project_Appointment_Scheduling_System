package com.appointment.service;

import com.appointment.domain.Administrator;
import com.appointment.domain.Appointment;
import com.appointment.repository.AdminRepository;

public class NotificationObserver implements AppointmentObserver {

    private final NotificationService notificationService;
    private final AdminRepository adminRepository;

    public NotificationObserver(NotificationService notificationService, AdminRepository adminRepository) {
        this.notificationService = notificationService;
        this.adminRepository = adminRepository;
    }

    @Override
    public void update(Appointment appointment, String eventType) {
        String userMessage = buildUserMessage(appointment, eventType);

        if (userMessage != null) {
            notificationService.notifyUser(appointment.getUser(), userMessage);
        }

        String adminMessage = buildAdminMessage(appointment, eventType);

        if (adminMessage != null) {
            for (Administrator admin : adminRepository.findAll()) {
                notificationService.notifyAdmin(admin, adminMessage);
            }
        }
    }

    private String buildUserMessage(Appointment appointment, String eventType) {
        switch (eventType) {
            case "BOOKED":
                return "Your appointment has been booked successfully for "
                        + appointment.getTimeSlot().getStartTime() + ".";

            case "ADMIN_CANCELLED":
                return "Your appointment on "
                        + appointment.getTimeSlot().getStartTime()
                        + " was cancelled by the administrator.";

            case "ADMIN_MODIFIED":
                return "Your appointment was modified by the administrator. "
                        + "The updated time is "
                        + appointment.getTimeSlot().getStartTime() + ".";

            case "USER_CANCELLED":
                return "Your appointment on "
                        + appointment.getTimeSlot().getStartTime()
                        + " has been cancelled successfully.";

            case "USER_MODIFIED":
                return "Your appointment has been modified successfully. "
                        + "The updated time is "
                        + appointment.getTimeSlot().getStartTime() + ".";

            default:
                return null;
        }
    }

    private String buildAdminMessage(Appointment appointment, String eventType) {
        switch (eventType) {
            case "USER_CANCELLED":
                return "User " + appointment.getUser().getFullName()
                        + " cancelled their appointment scheduled for "
                        + appointment.getTimeSlot().getStartTime() + ".";

            case "USER_MODIFIED":
                return "User " + appointment.getUser().getFullName()
                        + " modified their appointment. The updated time is "
                        + appointment.getTimeSlot().getStartTime() + ".";

            default:
                return null;
        }
    }
}