package com.appointment.repository;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.Administrator;

public class AdminRepository {

    private List<Administrator> admins = new ArrayList<>();

    public AdminRepository() {
        admins.add(new Administrator("admin", "1234"));
    }

    public Administrator findByUsername(String username) {

        for (Administrator admin : admins) {
            if (admin.getUsername().equals(username)) {
                return admin;
            }
        }

        return null;
    }
}