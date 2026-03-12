package com.appointment.domain;

public class User {
	 
	private final String name;

	    public User(String name) {
	        if (name == null || name.isBlank()) {
	            throw new IllegalArgumentException("User name cannot be empty.");
	        }
	        this.name = name;
	    }

	    public String getName() {
	        return name;
	    }
}
