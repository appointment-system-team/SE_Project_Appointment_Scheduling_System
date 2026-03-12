package com.appointment.service;

public class ParticipantLimitRule {

	 private static final int MAX_PARTICIPANTS = 5;

	    public boolean isValid(int participantCount) {
	        return participantCount > 0 && participantCount <= MAX_PARTICIPANTS;
	    }

	    public int getMaxParticipants() {
	        return MAX_PARTICIPANTS;
	    }
}
