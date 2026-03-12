package com.appointment.service;

public class DurationRule {
	
	private static final int MAX_DURATION_IN_MINUTES = 120;

	    public boolean isValid(int durationInMinutes) {
	        return durationInMinutes > 0 && durationInMinutes <= MAX_DURATION_IN_MINUTES;
	    }

	    public int getMaxDurationInMinutes() {
	        return MAX_DURATION_IN_MINUTES;
	    }
}
