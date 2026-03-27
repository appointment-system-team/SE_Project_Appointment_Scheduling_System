package com.appointment.service;

import com.appointment.domain.AppointmentCategory;

public class AppointmentCategoryRuleFactory {

    public AppointmentCategoryRule getRule(AppointmentCategory appointmentCategory) {
        if (appointmentCategory == null) {
            throw new IllegalArgumentException("Appointment category cannot be null.");
        }

        switch (appointmentCategory) {
            case INDIVIDUAL:
                return new IndividualAppointmentRule();
            case GROUP:
                return new GroupAppointmentRule();
            default:
                throw new IllegalArgumentException("Unsupported appointment category.");
        }
    }
}