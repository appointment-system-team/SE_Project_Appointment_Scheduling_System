package com.appointment.domain;

/**
 * Represents a user in the appointment scheduling system.
 * A user can log in, book appointments, and manage personal appointment data.
 */
public class User {

    /**
     * The username of the user.
     */
    private final String username;

    /**
     * The password of the user.
     */
    private final String password;

    /**
     * The full name of the user.
     */
    private final String fullName;

    /**
     * The email address of the user.
     */
    private final String email;

    /**
     * The phone number of the user.
     */
    private final String phoneNumber;

    /**
     * Creates a new user with the given information.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param fullName the full name of the user
     * @param email the email address of the user
     * @param phoneNumber the phone number of the user
     * @throws IllegalArgumentException if any field is null or blank
     */
    public User(String username, String password, String fullName, String email, String phoneNumber) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the username of the user.
     *
     * @return the user username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the user.
     *
     * @return the user password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the full name of the user.
     *
     * @return the user full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the user email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the phone number of the user.
     *
     * @return the user phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Validates the entered password against the stored user password.
     *
     * @param password the password to validate
     * @return true if the entered password matches the stored password, otherwise false
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}