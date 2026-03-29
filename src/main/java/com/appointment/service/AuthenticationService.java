package com.appointment.service;

import com.appointment.domain.Administrator;
import com.appointment.repository.AdminRepository;

public class AuthenticationService {

    private final AdminRepository repository;
    private Administrator loggedAdmin;

    public AuthenticationService(AdminRepository repository) {
        this.repository = repository;
    }

    public boolean login(String username, String password) {
        Administrator admin = repository.findByUsername(username);

        if (admin != null && admin.validatePassword(password)) {
            loggedAdmin = admin;
            return true;
        }

        return false;
    }

    public void logout() {
        loggedAdmin = null;
    }

    public boolean isLoggedIn() {
        return loggedAdmin != null;
    }

    public Administrator getLoggedAdmin() {
        return loggedAdmin;
    }
}