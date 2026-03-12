package com.appointment.domain;

public class Appointment {

	 private final User user;
	    private final TimeSlot timeSlot;
	    private final int durationInMinutes;
	    private final int participantCount;
	    private final AppointmentStatus status;

	    public Appointment(User user, TimeSlot timeSlot, int durationInMinutes, int participantCount) {
	        this.user = user;
	        this.timeSlot = timeSlot;
	        this.durationInMinutes = durationInMinutes;
	        this.participantCount = participantCount;
	        this.status = AppointmentStatus.CONFIRMED;
	    }

	    public User getUser() {
	        return user;
	    }

	    public TimeSlot getTimeSlot() {
	        return timeSlot;
	    }

	    public int getDurationInMinutes() {
	        return durationInMinutes;
	    }

	    public int getParticipantCount() {
	        return participantCount;
	    }

	    public AppointmentStatus getStatus() {
	        return status;
	    }
}
