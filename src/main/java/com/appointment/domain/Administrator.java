package com.appointment.domain;

/**
 * Represents an administrator in the appointment scheduling system
 * An administrator can log in and manage appointments
 */
public class Administrator {

    /**
     * The username of the administrator
     */
    private final String username;

    /**
     * The password of the administrator
     */
    private final String password;

    /**
     * The full name of the administrator
     */
    private final String fullName;

    /**
     * The email address of the administrator
     */
    private final String email;

    /**
     * The phone number of the administrator
     */
    private final String phoneNumber;

    /**
     * Creates a new administrator with the given information
     *
     * @param username the username of the administrator
     * @param password the password of the administrator
     * @param fullName the full name of the administrator
     * @param email the email address of the administrator
     * @param phoneNumber the phone number of the administrator
     * @throws IllegalArgumentException if any field is null or blank
     */
    public Administrator(String username, String password, String fullName, String email, String phoneNumber) {
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
     * Returns the username of the administrator
     *
     * @return the administrator username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the administrator
     *
     * @return the administrator password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the full name of the administrator
     *
     * @return the administrator full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the email address of the administrator
     *
     * @return the administrator email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the phone number of the administrator
     *
     * @return the administrator phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Validates the entered password against the administrator password
     *
     * @param password the password to validate
     * @return true if the entered password matches the stored password, otherwise false
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}