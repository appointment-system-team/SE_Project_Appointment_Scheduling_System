package com.appointment.service;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.Administrator;
import com.appointment.domain.User;

public class MockNotificationService implements NotificationService {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void notifyUser(User user, String message) {
        sentMessages.add("To User: " + user.getFullName() + " -> " + message);
    }

    @Override
    public void notifyAdmin(Administrator admin, String message) {
        sentMessages.add("To Admin: " + admin.getFullName() + " -> " + message);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }
}