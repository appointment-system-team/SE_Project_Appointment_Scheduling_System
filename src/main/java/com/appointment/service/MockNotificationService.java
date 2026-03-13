package com.appointment.service;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.User;

public class MockNotificationService implements NotificationService {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void notify(User user, String message) {

        sentMessages.add("To: " + user.getName() + " -> " + message);
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }
}