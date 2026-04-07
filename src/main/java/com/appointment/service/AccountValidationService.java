package com.appointment.service;

import com.appointment.repository.AdminRepository;
import com.appointment.repository.UserRepository;

public class AccountValidationService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AccountValidationService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.usernameExists(username) || adminRepository.usernameExists(username);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.emailExists(email) || adminRepository.emailExists(email);
    }

    public void validateNewUser(String username, String email) {
        if (isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (isEmailTaken(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }
    }

    public void validateNewAdmin(String username, String email) {
        if (isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (isEmailTaken(email)) {
            throw new IllegalArgumentException("Email already exists.");
        }
    }
}