package com.appointment.repository;

import java.util.ArrayList;
import java.util.List;

import com.appointment.domain.User;
import com.appointment.service.AccountValidationService;

public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        users.add(new User("zeina1", "1234", "Zeina Anabtawi", "zeina1@gmail.com", "0599000001"));
        users.add(new User("ameer1", "1234", "Ameer Abu Shams", "ameer1@gmail.com", "0599000002"));
    }

    public User findByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public User findByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
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

    public void addUser(User user, AccountValidationService validationService) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (validationService == null) {
            throw new IllegalArgumentException("Validation service cannot be null.");
        }

        validationService.validateNewUser(user.getUsername(), user.getEmail());
        users.add(user);
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}