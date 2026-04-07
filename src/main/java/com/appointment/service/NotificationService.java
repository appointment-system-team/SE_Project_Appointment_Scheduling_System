package com.appointment.service;

import com.appointment.domain.Administrator;
import com.appointment.domain.User;

public interface NotificationService {

    void notifyUser(User user, String message);

    void notifyAdmin(Administrator admin, String message);
}