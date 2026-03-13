package com.appointment.service;

import com.appointment.domain.User;

public interface NotificationService {

    void notify(User user, String message);
}