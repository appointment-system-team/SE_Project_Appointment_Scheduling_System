package com.appointment.repository;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.Administrator;
import com.appointment.service.AccountValidationService;

public class AdminRepository {

    private final List<Administrator> admins = new ArrayList<>();

    public AdminRepository() {
        admins.add(new Administrator(
                "admin",
                "1234",
                "System Admin",
                "admin@appointment.com",
                "0599000000"
        ));
    }

    public Administrator findByUsername(String username) {
        for (Administrator admin : admins) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }
        return null;
    }

    public Administrator findByEmail(String email) {
        for (Administrator admin : admins) {
            if (admin.getEmail().equals(email)) {
                return admin;
            }
        }
        return null;
    }

    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    public void addAdmin(Administrator admin, AccountValidationService validationService) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null.");
        }
        if (validationService == null) {
            throw new IllegalArgumentException("Validation service cannot be null.");
        }

        validationService.validateNewAdmin(admin.getUsername(), admin.getEmail());
        admins.add(admin);
    }

    public List<Administrator> findAll() {
        return new ArrayList<>(admins);
    }
}