package com.appointment.service;

import com.appointment.domain.User;
import com.appointment.repository.UserRepository;

public class UserAuthenticationService {

    private final UserRepository repository;
    private User loggedUser;

    public UserAuthenticationService(UserRepository repository) {
        this.repository = repository;
    }

    public boolean login(String username, String password) {
        User user = repository.findByUsername(username);

        if (user != null && user.validatePassword(password)) {
            loggedUser = user;
            return true;
        }

        return false;
    }

    public void logout() {
        loggedUser = null;
    }

    public boolean isLoggedIn() {
        return loggedUser != null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}